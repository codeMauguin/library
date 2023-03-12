package app.service;

import Message.RestResponse;
import app.dao.OrderDao;
import app.dao.QueryDao;
import app.inject.InjectBook;
import app.inject.InjectService;
import app.pojo.BorrowedTable;
import app.pojo.order;
import app.utils.SnowFlakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import pojo.PageData;
import pojo.PageInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.Assert.state;

@Service
public class OrderServiceImpl implements OrderService {
	private static final Integer timeout = -30;
	private final InjectService injectService;
	private final InjectBook injectBook;
	private final OrderDao orderDao;
	private final SnowFlakeIdWorker snowFlake;
	private final TransactionTemplate transactionTemplate;
	private final RedisTemplate<String, Integer> redisTemplates;
	
	private final QueryDao queryDao;
	
	public OrderServiceImpl(@Autowired InjectService injectService,
							@Autowired InjectBook injectBook,
							@Autowired OrderDao orderDao,
							@Autowired SnowFlakeIdWorker snowFlake,
							@Autowired TransactionTemplate transactionTemplate, @Qualifier(
			"integerRedisTemplate") @Autowired RedisTemplate<String,
																	Integer> redisTemplates,
							@Autowired QueryDao queryDao) {
		this.injectService =
				injectService;
		this.injectBook = injectBook;
		this.orderDao = orderDao;
		this.snowFlake = snowFlake;
		this.transactionTemplate = transactionTemplate;
		this.redisTemplates = redisTemplates;
		this.queryDao = queryDao;
	}
	
	@Override
	public Long createOrderId(Long id, List<Long> bookIds) {
		Long query = injectService.query(id);
		if (query == null) return null;
		// 查询是否存在订单
		Boolean is = orderDao.checkBooksHasCreate(query, bookIds);
		Assert.state(!Boolean.TRUE.equals(is), "重复借阅");
		long orderId = snowFlake.nextId();
		state(Boolean.TRUE.equals(orderDao.createOrder(query, orderId)), "订单创建失败");
		if (Boolean.FALSE.equals(transactionTemplate.execute(status -> {
			try {
				LocalDateTime localDateTime = LocalDateTime.now().plusDays(30);
				for (Long bookId : bookIds) {
					state(orderDao.createBorrowed(orderId, bookId, localDateTime),
							"创建借阅信息");
				}
				return true;
			} catch (Exception e) {
				status.setRollbackOnly();
				return false;
			}
		}))) {
			orderDao.failOrder(3, orderId, "预订单创建失败");
			state(false, "订单创建失败");
		}
		return orderId;
	}
	
	@Override
	public Boolean commitBook(Long orderId, Long userId) {
		// 查询用户是否能借阅书籍
		List<Long> bookIds = orderDao.queryBookIds(orderId);
		CompletableFuture<Boolean> uCompletableFuture = CompletableFuture.supplyAsync(() -> {
			RestResponse restResponse = injectService.queryCard(userId);
			if (restResponse.getCode() != 1000)
				return false;
			Map<String, Integer> data = (LinkedHashMap<String, Integer>) restResponse.getData();
			return data.getOrDefault("timeout", 0) <= 0
						   && data.getOrDefault("total", 0) - data.getOrDefault("borrowed", 0)
									  >= bookIds.size();
		});
		// 查询订单状态
		CompletableFuture<Boolean> checkAndSetOrder =
				CompletableFuture.supplyAsync(() -> orderDao.updateOrder(orderId));
		// 查询库存并放入redis
		CompletableFuture<Boolean> queryStock = CompletableFuture.supplyAsync(() -> {
			List<Long> task = new ArrayList<>();
			for (Long aLong : bookIds) {
				Integer value = redisTemplates.opsForValue()
											  .getAndExpire(String.format(key(aLong)),
													  2, TimeUnit.MINUTES);
				if (value == null) {
					task.add(aLong);
					continue;
				}
				if (value <= 0) return false;
			}
			List<Integer> integers = injectBook.queryBooks(task);
			for (int j = 0, integersSize = integers.size(); j < integersSize; j++) {
				final Integer i = integers.get(j);
				redisTemplates.opsForValue().setIfAbsent(key(task.get(j)),
						i);
				if (i <= 0) return false;
			}
			return true;
		});
		CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(uCompletableFuture
				, checkAndSetOrder, queryStock);
		Boolean join = voidCompletableFuture
							   .thenApply(unused -> uCompletableFuture.join() && checkAndSetOrder.join() && queryStock.join())
							   .join();
		List<Long> suc = new ArrayList<>();
		try {
			state(join, "不符合借阅条件");
			// 扣除订单
			// 扣减成功放入
			for (Long bookId : bookIds) {
				Long decrement = redisTemplates.opsForValue().decrement(key(bookId));
				if (decrement == null || decrement < 0) {
					break;
				}
				suc.add(bookId);
			}
			if (suc.size() != bookIds.size()) {
				throw new IllegalStateException("库存不足");
			}
			state(injectService.deductionOfBorrowed(userId, suc.size()), "用户余额扣减失败");
			Boolean aBoolean = injectBook.deductionOfInventory(suc);
			if (!aBoolean) {
				// 将用户扣减的加回来
				injectService.deductionOfBorrowed(userId, -suc.size());
			}
			state(aBoolean, "库存扣减失败");
			state(orderDao.finalOrder(orderId), "订单刷新失败");
			return transactionTemplate.execute(status -> {
				
				try {
					for (Long bookId : suc) {
						state(orderDao.updateBorrowed(orderId, bookId, 0),
								"创建借阅信息");
					}
					return true;
				} catch (Exception e) {
					status.setRollbackOnly();
					rollback(suc);
					// 把库存改回来
					injectBook.incrementOfInventory(suc);
					injectService.deductionOfBorrowed(userId, -suc.size());
					orderDao.failOrder(2, orderId, e.getMessage());
				}
				return false;
			});
		} catch (RuntimeException e) {
			// 将订单设置为失败状态
			rollback(suc);
			orderDao.failOrder(2, orderId, e.getMessage());
			throw e;
		}
	}
	
	@Override
	public PageData<order> getOrders(PageInfo pageInfo, Long id) {
		Long query = injectService.query(id);
		return PageData.response(pageInfo, page -> queryDao.getOrders(pageInfo, query),
				() -> queryDao.getOrderCount(query));
	}
	
	@Override
	public List<order> getOrders(List<LocalDateTime> timer, Long orderId, Integer status,
								 Long id) {
		Long query = injectService.query(id);
		if (timer != null && timer.size() == 2) {
			return queryDao.getOrdersByCondition(timer.get(0), timer.get(1), orderId, status,
					query);
		}
		return queryDao.getOrdersByCondition(null, null, orderId, status,
				query);
		
	}
	
	@Override
	public PageData<BorrowedTable> getBorrowed(PageInfo pageInfo, List<Integer> status, Long id) {
		Long query = injectService.query(id);
		state(query != null && query > 0, "查询card失败");
		return PageData.response(pageInfo,
				page -> {
					List<BorrowedTable> borrowers = queryDao.getBorrowers(page, status, query);
					// 检查是否超时
					LocalDateTime now = LocalDateTime.now();
					borrowers.forEach(borrowedTable -> {
						if (borrowedTable.getStatus() == 0 && borrowedTable
																	  .getReturnTime()
																	  .isBefore(now)) {
							orderDao.timeoutBorrowed(borrowedTable.getId());
							borrowedTable.setStatus(-1);
						}
					});
					return borrowers;
				},
				() -> queryDao.getBorrowedSize(status, query)
								);
	}
	
	@Override
	public Boolean returnBook(Long id, Long userId) {
		Long query = injectService.query(userId);
		BorrowedTable borrowed = queryDao.getBorrowed(id, query);
		if (borrowed == null) return false;
		state(orderDao.returnBook(id), "还书失败");
		state(injectService.deductionOfBorrowed(userId, -1), "还书失败");
		state(injectBook.incrementOfInventory(List.of(borrowed.getBookId())), "书籍返还失败");
		return true;
	}
	
	@Override
	public List<Long> getHasBorrowed(Long userId) {
		Long cardId = injectService.query(userId);
		return queryDao.getHasBorrowed(cardId);
	}
	
	@Override
	public LocalDateTime renewal(Long id, Long cid) {
		Long cardId = injectService.query(id);
		BorrowedTable borrowedTable = new BorrowedTable();
		borrowedTable.setRenewal(1);
		borrowedTable.setId(cid);
		borrowedTable.setOrderId(cardId);
		Boolean hasBorrowed = queryDao.renewal(borrowedTable);
		return hasBorrowed ? borrowedTable.getReturnTime() : null;
	}
	
	public String key(Long id) {
		return String.format("[%d]%s", id, ":stock");
	}
	
	public void rollback(List<Long> bookIds) {
		for (Long aLong : bookIds) {
			redisTemplates.opsForValue().increment(key(aLong));
		}
	}
}
