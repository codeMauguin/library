package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.service.CalculateTotalSales;
import app.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pojo.PageInfo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderCenterImpl implements OrderCenter {
	private final OrderService orderService;
	private final CalculateTotalSales calculateTotalSales;
	
	public OrderCenterImpl(@Autowired OrderService orderService,
						   @Autowired CalculateTotalSales calculateTotalSales) {
		this.orderService = orderService;
		this.calculateTotalSales = calculateTotalSales;
	}
	
	@Override
	public RestResponse createOrder(Long id, List<Long> bookIds) {
		Long orderServiceOrderId = orderService.createOrderId(id, bookIds);
		if (orderServiceOrderId == null || orderServiceOrderId == -1L) {
			return RestResponse.response(ResponseCode.FAIL_CREATE_ORDER);
		}
		return RestResponse.response(
				orderServiceOrderId,
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse commit(Long id, Long orderId) {
		try {
			return RestResponse.response(orderService.commitBook(orderId, id),
					ResponseCode.SUCCESS);
		} catch (RuntimeException e) {
			return RestResponse.response(ResponseCode.FAIL_COMMIT_ORDER.setMessage(e.getMessage()));
		}
	}
	
	@Override
	public RestResponse getOrders(PageInfo pageInfo, Long id) {
		return RestResponse.response(orderService.getOrders(pageInfo, id),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse getOrdersByCondition(List<Long> timer, Long orderId,
											 Integer status, Long id) {
		List<LocalDateTime> timers = new ArrayList<>();
		if (timer != null && !timer.isEmpty()) {
			for (Long aLong : timer) {
				timers.add(Instant
								   .ofEpochMilli(aLong)
								   .atZone(ZoneId.systemDefault())
								   .toLocalDateTime());
			}
		}
		return RestResponse.response(orderService.getOrders(timers, orderId, status, id),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse getBorrowed(Long id, PageInfo pageInfo, List<Integer> status) {
		return RestResponse.response(orderService.getBorrowed(pageInfo,
						status == null || status.isEmpty() ? null : status, id),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse renewal(Long id, Long cid) {
		LocalDateTime res = orderService.renewal(id, cid);
		return res == null ? RestResponse.response(ResponseCode.FAIL_RENEWAL) :
					   RestResponse.response(res, ResponseCode.SUCCESS);
	}
	
	@Override
	// 未实现的方法。
	public RestResponse returnBook(Long id, Long userId) {
		return RestResponse.response(orderService.returnBook(id, userId),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse getHasBorrowed(Long userId) {
		return RestResponse.response(orderService.getHasBorrowed(userId),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse statistics(Long userId) {
		return RestResponse.response(calculateTotalSales.getCount(userId), ResponseCode.SUCCESS);
	}
}
