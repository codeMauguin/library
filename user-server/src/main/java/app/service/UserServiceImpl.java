package app.service;

import app.dao.AccountDao;
import app.dao.UserDao;
import app.pojo.UserCard;
import app.utils.SignUtil;
import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.user;

@Service
public class UserServiceImpl implements UserService {
	private final UserDao userDao;
	private final AccountDao accountDao;
	
	private final AccountService accountService;
	
	public UserServiceImpl(@Autowired UserDao userDao, @Autowired AccountDao accountDao,
						   @Autowired AccountService accountService) {
		this.userDao = userDao;
		this.accountDao = accountDao;
		this.accountService = accountService;
	}
	
	@Override
	public user query(Long id) {
		return userDao.queryById(id);
	}
	
	@Override
	public UserCard queryCard(Long id) {
		return userDao.queryCardById(id);
	}
	
	@Override
	public Long queryCardId(Long id) {
		return userDao.queryCardId(id);
	}
	
	@Override
	public Boolean deductionOfBorrowed(Integer size, Long userId) {
		return userDao.deductionOfBorrowed(size, userId);
	}
	
	@Override
	public user queryUser(Long cardId) {
		return userDao.queryUser(cardId);
	}
	
	@Override
	public Boolean revise(String name, String password, Long id) {
		Boolean ans = true;
		if (password != null && !password.isEmpty()) {
			// 修改密码
			String saltById = accountDao.getSaltById(id);
			// 将当前用户下线
			accountService.logout(id);
			ans = accountDao.revise(id, SignUtil.signing(password, saltById));
		}
		if (name == null || name.isEmpty()) return ans;
		if (StringUtils.isNumber(name) || !ans) {
			return false;
		}
		try {
			return userDao.revise(name, id);
		} catch (
				  Exception e
		) {
			return false;
		}
	}
}
