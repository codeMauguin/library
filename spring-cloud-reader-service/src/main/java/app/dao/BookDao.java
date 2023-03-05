package app.dao;

import app.pojo.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pojo.PageInfo;

import java.util.List;

@Mapper
public interface BookDao {
	/**
	 * 获取书籍的大小，但前提是用户是读者。
	 *
	 * @param isReader 如果用户是读者，则为 true；如果用户是作者，则为 false。
	 * @return 数据库中的书籍数量。
	 */
	Integer getBookSize(@Param("isReader") Boolean isReader);
	
	/**
	 * > 获取图书列表，列表分页，列表按指定字段排序
	 *
	 * @param isReader 是否查询用户借阅的图书信息
	 * @param pageInfo pageInfo对象用于存储页面信息，如当前页码、每页记录数、总记录数等。
	 * @return pageInfo 对象的列表。
	 */
	List<Book> getBooks(@Param("isReader") boolean isReader, @Param("pageInfo") PageInfo pageInfo);
	
	Book getBook(@Param("bookId") Long bookId);
	
	List<Integer> getBookStock(@Param("bookIds") List<Long> bookIds);
	
	Boolean deductionOfInventory(@Param("bookId") Long bookId);
	
	Boolean incrementOfInventory(@Param("bookId") Long bookId);
	
	List<String> queryBookName(String keyword, String condition);
	
	List<Book> getBooksBySearch(@Param("search") String search, @Param("mode") String mode);
	
	String queryBookNameOne(@Param("bookId") Long bookId);
}
