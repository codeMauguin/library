package app.service;

import app.dao.AdminOrderDao;
import app.inject.InjectBook;
import app.inject.InjectService;
import app.pojo.AdminBorrowedTable;
import com.alibaba.nacos.shaded.com.google.common.base.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.PageData;
import pojo.PageInfo;
import pojo.user;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
	private final AdminOrderDao adminOrderDao;
	private final InjectBook injectBook;
	private final InjectService injectService;
	private final Supplier<Integer> queryAllSize;
	
	public AdminServiceImpl(@Autowired AdminOrderDao adminOrderDao,
							@Autowired InjectBook injectBook,
							@Autowired InjectService injectService) {
		this.adminOrderDao =
				adminOrderDao;
		this.injectBook = injectBook;
		this.queryAllSize = adminOrderDao::queryAll_Size;
		this.injectService = injectService;
		
	}
	
	@Override
	public Integer getBorrowed(Long id) {
		return adminOrderDao.getBorrowed(id);
	}
	
	@Override
	public PageData<AdminBorrowedTable> getAllBorrowed(PageInfo pageInfo) {
		return PageData.response(pageInfo, page -> {
			List<AdminBorrowedTable> adminBorrowedTables = adminOrderDao.queryAll(page);
			return adminBorrowedTables.stream()
									  .peek(adminBorrowedTable -> {
										  adminBorrowedTable.setBookName(injectBook.queryBookName(adminBorrowedTable.getBookId()));
									  }).
									  peek(adminBorrowedTable -> {
										  user objectObjectMap =
												  injectService.queryUser(adminBorrowedTable.getCardId());
										  adminBorrowedTable.setUsername(
												  objectObjectMap.getName()
																		);
										  adminBorrowedTable.setUserId(objectObjectMap.getId());
									  }).toList();
		}, queryAllSize);
	}
	
	@Override
	public Boolean flagBorrowed(Long id, Integer state) {
		//
		if (state == 1) {
			// 更新书籍库存、用户借阅数
			return returnBook(id);
		}
		return adminOrderDao.updateBorrowed(id, state, null);
	}
	
	
	/**
	 * > 该函数用于还书
	 *
	 * @param id 要归还的书的id
	 * @return returnBook 方法返回一个布尔值。
	 */
	private Boolean returnBook(Long id) {
		AdminBorrowedTable one = adminOrderDao.getOne(id);
		user user = injectService.queryUser(one.getCardId());
		if (user == null) return false;
		Boolean aBoolean = injectService.deductionOfBorrowed(user.getId(), -1);
		Boolean aBoolean1 = injectBook.incrementOfInventory(List.of(one.getBookId()));
		return aBoolean && aBoolean1 && adminOrderDao.updateBorrowed(id, 1, LocalDateTime.now());
	}
}
