package app.service;

import app.pojo.UserCard;
import pojo.user;

public interface UserService {
	user query(Long id);
	
	UserCard queryCard(Long id);
	
	Long queryCardId(Long id);
	
	Boolean deductionOfBorrowed(Integer size, Long userId);
	
	user queryUser(Long cardId);
	
	Boolean revise(String name, String password, Long id);
}
