package app.service;

import app.dao.BookDao;
import app.pojo.Book;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.PageData;
import pojo.PageInfo;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class BookServiceImpl implements BookService {
	private final BookDao bookDao;
	
	private final SqlSessionFactory sqlSessionFactory;
	
	public BookServiceImpl(@Autowired BookDao bookDao,
						   @Autowired SqlSessionFactory sqlSessionFactory) {
		this.bookDao = bookDao;
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	/**
	 * > 获取书籍列表，其中列表为书籍的一页，其中页面由pageInfo对象定义，书籍由oderBy字符串排序
	 *
	 * @param b        true 表示获取特价图书，false 表示获取非特价图书。
	 * @param pageInfo 包含页码和页面大小的 pageInfo 对象。
	 * @return pageInfo 对象的列表。
	 */
	@Override
	public PageData<Book> getBooks(boolean b, PageInfo pageInfo) {
		// 避免数量为0的情况
		boolean isInit = !(pageInfo.getSize() == null || pageInfo.getSize() < 0);
		return PageData.response(pageInfo,
				page -> bookDao.getBooks(b, page),
				() -> getBookSize(b));
	}
	
	@Override
	public Integer getBookSize(Boolean isReader) {
		return bookDao.getBookSize(isReader);
	}
	
	@Override
	public Book getBook(Long bookId) {
		return bookDao.getBook(bookId);
	}
	
	@Override
	public List<Integer> getBookStock(List<Long> bookIds) {
		return bookIds.isEmpty() ? List.of() : bookDao.getBookStock(bookIds);
		
	}
	
	@Override
	public Boolean deductionOfInventory(List<Long> bookIds) {
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		BookDao mapper = sqlSession.getMapper(BookDao.class);
		try {
			for (Long bookId : bookIds) {
				mapper.deductionOfInventory(bookId);
			}
			List<BatchResult> batchResults = sqlSession.flushStatements();
			BatchResult batchResult = batchResults.get(batchResults.size() - 1);
			int[] updateCounts = batchResult.getUpdateCounts();
			return IntStream.of(updateCounts).allMatch(result -> result > 0 || result == -2);
		} catch (Exception e) {
			sqlSession.rollback();
		} finally {
			sqlSession.commit();
			sqlSession.close();
		}
		return false;
	}
	
	@Override
	public Boolean incrementOfInventory(List<Long> bookIds) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
			BookDao mapper = sqlSession.getMapper(BookDao.class);
			try {
				for (Long bookId : bookIds) {
					mapper.incrementOfInventory(bookId);
				}
				List<BatchResult> batchResults = sqlSession.flushStatements();
				int[] updateCounts = batchResults.get(batchResults.size() - 1).getUpdateCounts();
				return IntStream.of(updateCounts).allMatch(result -> result == 1 || result == -2);
			} catch (Exception e) {
				sqlSession.rollback();
			} finally {
				sqlSession.commit();
				sqlSession.close();
			}
		}
		return false;
	}
	
	@Override
	public List<String> queryBookName(String keyword, String condition) {
		return bookDao.queryBookName(keyword, condition);
	}
	
	@Override
	public List<Book> queryBook(String search, String mode) {
		return bookDao.getBooksBySearch(search, mode);
	}
	
	@Override
	public String queryBookName(Long bookId) {
		return bookDao.queryBookNameOne(bookId);
	}
	
}
