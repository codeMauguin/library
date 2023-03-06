package app.service;

import app.pojo.BorrowedTable;
import app.pojo.order;
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
	
	/**
	 * “如果给定用户当前已签出图书，则返回具有给定 ID 的图书。”
	 *
	 * @param id     要归还的图书的 ID。
	 * @param userId 归还图书的用户的用户 ID。
	 * @return 一个布尔值。
	 */
	Boolean returnBook(Long id, Long userId);
	
	/**
	 * 获取具有给定 userId 的用户借阅的所有书籍的列表。
	 *
	 * @param userId 用户的 ID。
	 * @return 用户借阅的图书 ID 列表。
	 */
	List<Long> getHasBorrowed(Long userId);
	
	/**
	 * 如果具有给定 userId 的用户已经借过这本书且这本书没有续签过，则续签这本书
	 *
	 * @param id  您要归还的图书的 ID。
	 * @param cid 归还图书的用户的 ID。
	 * @return 布尔值
	 */
	LocalDateTime renewal(Long id, Long cid);
}
