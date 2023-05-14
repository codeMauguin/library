package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;
import pojo.PageInfo;

@RestController
public class AdminCenterImpl implements AdminCenter {
	private final static short USER_BACK_LIST = 1;
	private final static short USER_DEL = -1;
	public final static short USER_STATE = 0;
	private final AdminService adminService;
	
	public AdminCenterImpl(
			@Autowired AdminService adminService) {this.adminService = adminService;}
	
	@Override
	public RestResponse getUsers( PageInfo pageInfo) {
		return RestResponse.response(adminService.getUsers(pageInfo), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse getUsersBack( PageInfo pageInfo, Boolean isNormal, Boolean isBlack,
									 Long searchId) {
		return RestResponse.response(adminService.getUsersBack(pageInfo, isNormal, isBlack,
						searchId),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse update_user_total( Long id,  Integer total) {
		return RestResponse.response(adminService.updateUserTotal(id, total),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse pullIntoTheBlacklist( Long id) {
		return RestResponse.response(adminService.updateUserState(id, USER_BACK_LIST),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse deleteUser( Long id) {
		Assert.state(adminService.updateUserState(id, USER_DEL),"删除失败");
		return RestResponse.response("删除成功",
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse restoreInto( Long id) {
		return RestResponse.response(adminService.restoreInto(id),
				ResponseCode.SUCCESS);
	}
}
