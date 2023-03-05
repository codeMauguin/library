package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.pojo.User;
import app.service.AccountService;
import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAccountController implements AccountController {
	private final AccountService accountService;
	
	public UserAccountController(@Qualifier("accountImpl") @Autowired AccountService accountService) {
		this.accountService = accountService;
	}
	
	@Override
	public RestResponse login(Long id, String name, String password) {
		User user = accountService.login(id, name, password);
		if (user == null) {
			return RestResponse.response(ResponseCode.LOGIN_FAIL);
		}
		return RestResponse.response(user, ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse registry(String name, String password) {
		return registry(name, password, "reader");
	}
	
	@Override
	public RestResponse registry(String name, String password, String permission) {
		if (StringUtils.isNumber(name)) {
			return RestResponse.response(ResponseCode.REGISTRY_NAME);
		}
		Long registry = accountService.registry(name, password, permission);
		if (registry != null) {
			return RestResponse.response(registry, ResponseCode.SUCCESS);
		}
		return RestResponse.response(ResponseCode.REGISTRY);
	}
	
	@Override
	public RestResponse logout(Long id) {
		accountService.logout(id);
		return RestResponse.response(ResponseCode.SUCCESS);
	}
}
