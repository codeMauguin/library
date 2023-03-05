package app.views;

import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pojo.user;

@RestController
public class InjectControllerImpl implements InjectController {
	private final UserService userService;
	
	public InjectControllerImpl(@Autowired UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public user query(Long id) {
		return userService.query(id);
	}
	
	@Override
	public Long queryCardId(Long id) {
		return userService.queryCardId(id);
	}
	
	@Override
	public user queryUser(Long cardId) {
		return userService.queryUser(cardId);
	}
	
	@Override
	public Boolean deductionOfBorrowed(Integer size, Long userId) {
		return userService.deductionOfBorrowed(size, userId);
	}
}
