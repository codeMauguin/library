package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.pojo.TodayBorrowed;
import app.pojo.TodayReturn;
import app.pojo.TotalOrder;
import app.service.AdminService;
import app.service.CalculateTotalSales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pojo.PageInfo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminOrderCenterImpl implements AdminOrderCenter {
	
	private final AdminService adminService;
	private final CalculateTotalSales calculateTotalSales;
	
	public AdminOrderCenterImpl(@Autowired AdminService adminService,
								@Autowired CalculateTotalSales calculateTotalSales) {
		this.adminService =
				adminService;
		this.calculateTotalSales = calculateTotalSales;
	}
	
	@Override
	public RestResponse getAllBorrowed(PageInfo page) {
		return RestResponse.response(adminService.getAllBorrowed(page), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse flag_borrowed(Long id, Integer state) {
		return RestResponse.response(adminService.flagBorrowed(id, state), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse statistic() {
		Map<String, Object> ans = new HashMap<>();
		TodayBorrowed borrowedCount = calculateTotalSales.getBorrowedCount();
		TodayReturn todayReturn = calculateTotalSales.getReturnCount();
		TotalOrder totalOrder = calculateTotalSales.getTotalOrder();
		ans.put("borrowed", borrowedCount);
		ans.put("returnData", todayReturn);
		ans.put("totalBorrow", totalOrder);
		ans.put("statistic", calculateTotalSales.geostationary(LocalDate.now().getYear()));
		return RestResponse.response(ans, ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse statistic_year(Integer year) {
		return RestResponse.response(calculateTotalSales.geostationary(year),
				ResponseCode.SUCCESS);
	}
}
