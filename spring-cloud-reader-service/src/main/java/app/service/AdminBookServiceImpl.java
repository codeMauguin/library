package app.service;

import app.dao.AdminBookDao;
import app.pojo.Book;
import app.pojo.Inventory;
import app.utils.SnowFlakeIdWorker;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.IntStream.of;
import static java.util.stream.IntStream.range;

@Service
public class AdminBookServiceImpl implements AdminBookService {
	private final static Integer sharding = 500;
	private final SnowFlakeIdWorker snowFlakeIdWorker;
	private final SqlSessionFactory sqlSessionFactory;
	private final AdminBookDao adminBookDao;
	
	public AdminBookServiceImpl(@Autowired SnowFlakeIdWorker snowFlakeIdWorker,
								@Autowired SqlSessionFactory sqlSessionFactory,
								@Autowired AdminBookDao adminBookDao) {
		this.snowFlakeIdWorker =
				snowFlakeIdWorker;
		this.sqlSessionFactory = sqlSessionFactory;
		this.adminBookDao = adminBookDao;
	}
	
	@Override
	public Boolean uploadBooks(List<Book> tasks) {
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		try {
			AdminBookDao mapper = sqlSession.getMapper(AdminBookDao.class);
			List<CompletableFuture<Void>> pools = new ArrayList<>();
			AtomicBoolean status = new AtomicBoolean(true);
			int index = 0;
			while (index < tasks.size()) {
				int end = Math.min(tasks.size(), index + sharding);
				final int finalIndex = index;
				pools.add(CompletableFuture.supplyAsync(() -> {
					range(finalIndex, end)
							.peek(t -> {
								Book book = tasks.get(t);
								book.setId(snowFlakeIdWorker.nextId());
								if (book.getInventory() == null || book
																		   .getInventory()
																		   .getTotal() == null) {
									Inventory i = new Inventory();
									i.setBookId(book.getId());
									i.setTotal(0);
									book.setInventory(i);
								} else {
									book.getInventory().setBookId(book.getId());
								}
							})
							.forEach(t -> {
								try {
									mapper.insert(tasks.get(t));
									mapper.initStock(tasks.get(t).getInventory());
								} catch (Exception e) {
									status.set(false);
								}
							});
					List<BatchResult> batchResults = sqlSession.flushStatements();
					BatchResult batchResult = batchResults.get(batchResults.size() - 1);
					int[] updateCounts = batchResult.getUpdateCounts();
					boolean b = of(updateCounts)
										.allMatch(value -> value == -2 || value > 0);
					if (!b) status.set(false);
					return null;
				}));
				index = end;
			}
			CompletableFuture<Void> voidCompletableFuture =
					CompletableFuture.allOf(pools.toArray(new CompletableFuture[0]));
			Void unused = voidCompletableFuture.get();
			if (Boolean.TRUE.equals(status.get())) {
				return true;
			}
			sqlSession.rollback(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			sqlSession.commit();
			sqlSession.rollback();
			sqlSession.close();
		}
		return false;
	}
	
	@Override
	public Boolean shelvesBooks(Long id) {
		return adminBookDao.updateBookState(id, 0);
	}
	
	@Override
	public Boolean takeDownBooks(Long id) {
		return adminBookDao.updateBookState(id, 1);
	}
	
	@Override
	@Transactional(rollbackFor = {Exception.class, SQLException.class})
	public Boolean updateBooks(Book book) {
		book.setName(StringUtils.hasText(book.getName()) ? book.getName() : null);
		book.setName(StringUtils.hasText(book.getInfo()) ? book.getInfo() : null);
		book.setName(StringUtils.hasText(book.getPress()) ? book.getPress() : null);
		book.setName(StringUtils.hasText(book.getAuthor()) ? book.getAuthor() : null);
		book.setName(StringUtils.hasText(book.getCategory()) ? book.getCategory() : null);
		Boolean aBoolean = adminBookDao.updateBook(book);
		if (aBoolean && book.getInventory() != null && book.getInventory().getTotal() != null) {
			return adminBookDao.updateStock(book.getId(), book
																  .getInventory()
																  .getTotal());
		}
		return aBoolean;
	}
	
	@Override
	public List<Object> report() {
		// 查询借阅信息
		// 查询总借出
		// 查询用户数
		// 统计日借阅数
		//
		return null;
	}
}
