package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {
	
	private final UserService userService;
	
	public UserControllerImpl(@Autowired UserService userService) {
		this.userService = userService;
	}
	
	
	@Override
	public RestResponse getCard(Long id) {
		return RestResponse.response(userService.queryCard(id), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse revise(String name, String password, Long id) {
		if (!StringUtils.hasText(name) && !StringUtils.hasText(password))
			return RestResponse.response(ResponseCode.PARAM_ERROR);
		return RestResponse.response(userService.revise(name, password, id), ResponseCode.SUCCESS);
	}
}
