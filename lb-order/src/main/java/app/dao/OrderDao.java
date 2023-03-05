package app.dao;


import app.pojo.order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDao {
	
	
	Boolean createOrder(@Param("id") Long id, @Param("orderId") long orderId);
	
	Boolean updateOrder(@Param("orderId") Long orderId);
	
	Boolean finalOrder(@Param("orderId") Long orderId);
	
	Boolean failOrder(@Param("ss") Integer status,
					  @Param("orderId") Long orderId,
					  @Param("reason") String reason);
	
	/**
	 * "Create a new borrowed book for the given order and book, with the given return date."
	 * <p>
	 * The @Param annotation is used to specify the name of the parameter in the SQL query
	 *
	 * @param orderId       The id of the order
	 * @param bookId        The id of the book that is being borrowed.
	 * @param localDateTime The date and time when the book was borrowed.
	 * @return Boolean
	 * @implSpec 创建预订单信息
	 */
	Boolean createBorrowed(@Param("orderId") Long orderId, @Param("bookId") Long bookId, @Param(
			"localDateTime") LocalDateTime localDateTime);
	
	Boolean timeoutOrder(@Param("order") order order);
	
	List<Long> queryBookIds(@Param("orderId") Long orderId);
	
	Boolean updateBorrowed(@Param("orderId") Long orderId, @Param("bookId") Long bookId, @Param(
			"status") Integer status);
	
	Boolean returnBook(@Param("id") Long id);
	
	Boolean checkBooksHasCreate(@Param("query") Long query, @Param("bookIds") List<Long> bookIds);
}
