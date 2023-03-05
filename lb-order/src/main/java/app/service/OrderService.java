package app.service;

import app.pojo.BorrowedTable;
import app.pojo.order;
import org.apache.ibatis.annotations.Param;
import pojo.PageData;
import pojo.PageInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
	
	/**
	 * Create an order id by adding the id to the list of order ids.
	 *
	 * @param bookIds The list of order ids that are already in the database.
	 * @param id      The id of the order you want to create.
	 * @return A Long
	 * @implNote 创建订单id-检查同时是否右正在创建的订单如果右存在同时期订单则取消当前订单
	 */
	Long createOrderId(Long id, List<Long> bookIds);
	
	Boolean commitBook(Long orderId, Long userId);
	
	
	/**
	 * Get a list of orders for a given customer, with pagination.
	 *
	 * @param pageInfo The pageInfo object is used to get the page number and the number of
	 *                 records per
	 *                 page.
	 * @param id       The id of the user
	 * @return A list of orders.
	 */
	PageData<order> getOrders(PageInfo pageInfo, Long id);
	
	List<order> getOrders(List<LocalDateTime> timer, Long orderId, Integer status, Long id);
	
	PageData<BorrowedTable> getBorrowed(PageInfo pageInfo, List<Integer> status, Long id);
	
	Boolean returnBook(@Param("id") Long id, @Param("userId") Long userId);
	
	List<Long> getHasBorrowed(Long userId);
	
	LocalDateTime renewal(Long id, Long cid);
}
