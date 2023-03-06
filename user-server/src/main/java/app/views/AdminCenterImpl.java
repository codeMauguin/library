package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pojo.PageInfo;

@RestController
public class AdminCenterImpl implements AdminCenter {
	private final AdminService adminService;
	
	public AdminCenterImpl(
			@Autowired AdminService adminService) {this.adminService = adminService;}
	
	@Override
	public RestResponse getUsers(PageInfo pageInfo) {
		return RestResponse.response(adminService.getUsers(pageInfo), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse getUsersBack(PageInfo pageInfo, Boolean isNormal, Boolean isBlack,
									 Long searchId) {
		return RestResponse.response(adminService.getUsersBack(pageInfo, isNormal, isBlack,
						searchId),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse update_user_total(Long id, Integer total) {
		return RestResponse.response(adminService.updateUserTotal(id, total),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse pullIntoTheBlacklist(Long id) {
		return RestResponse.response(adminService.pullIntoTheBlacklist(id),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse restoreInto(Long id) {
		return RestResponse.response(adminService.restoreInto(id),
				ResponseCode.SUCCESS);
	}
}
