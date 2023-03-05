package app.service;

import app.pojo.Book;
import pojo.PageData;
import pojo.PageInfo;

import java.util.List;

public interface BookService {
	/**
	 * 如果 isReader 参数为真，则返回书中的页数。如果 isReader 参数为 false，则返回书中的章节数。
	 *
	 * @param isReader 如果用户是读者，则为 true；如果用户是作者，则为 false。
	 * @return 图书馆的图书数量。
	 */
	Integer getBookSize(Boolean isReader);
	
	/**
	 * > 获取书籍列表，其中列表为书籍的一页，其中页面由pageInfo对象定义，书籍由oderBy字符串排序
	 *
	 * @param b        true 表示获取特价图书，false 表示获取非特价图书。
	 * @param pageInfo 包含页码和页面大小的 pageInfo 对象。
	 * @param oderBy   order by 子句。
	 * @return pageInfo 对象的列表。
	 */
	PageData<Book> getBooks(boolean b, PageInfo pageInfo);
	
	/**
	 * Get a book by its ID.
	 *
	 * @param bookId The id of the book to be retrieved.
	 * @return A Book object.
	 */
	Book getBook(Long bookId);
	
	List<Integer> getBookStock(List<Long> bookIds);
	
	Boolean deductionOfInventory(List<Long> bookIds);
	
	Boolean incrementOfInventory(List<Long> bookIds);
	
	List<String> queryBookName(String keyword, String condition);
	
	List<Book> queryBook(String search, String mode);
	
	String queryBookName(Long bookId);
}
