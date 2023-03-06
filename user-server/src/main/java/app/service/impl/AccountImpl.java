package app.service.impl;

import app.dao.AccountDao;
import app.dao.UserDao;
import app.pojo.Backer;
import app.pojo.User;
import app.pojo.UserAccount;
import app.service.AccountService;
import app.utils.SignUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pojo.PageInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class AccountImpl implements AccountService {
	private final static String pattern = "[%d]:[$user$]";
	private final static String TOKEN_RES_KEY = "bl";
	private final AccountDao accountDao;
	private final RedisTemplate<String, String> redisTemplate;
	
	private final TransactionTemplate transactionTemplate;
	
	private final UserDao userDao;
	
	public AccountImpl(@Autowired AccountDao accountDao,
					   @Autowired RedisTemplate<String, String> redisTemplate,
					   @Autowired TransactionTemplate transactionTemplate,
					   @Autowired UserDao userDao) {
		this.accountDao = accountDao;
		this.redisTemplate = redisTemplate;
		this.transactionTemplate = transactionTemplate;
		this.userDao = userDao;
	}
	
	/**
	 * 如果密码为空或者name和id都为空，则返回false，否则返回其他登录函数的结果。
	 *
	 * @param id       用户的 ID。
	 * @param name     用户的名称。
	 * @param password 用于检查用户密码的密码。
	 * @return 一个布尔值。
	 */
	@Override
	public User login(Long id, String name, String password) {
		boolean nameLogin = StringUtils.hasText(name);
		if (!StringUtils.hasText(password) || (!nameLogin && id == null)) return null;
		return nameLogin ? login(name, password) : login(id, password);
	}
	
	private User login(String name, String password) {
		Long id = accountDao.queryId(name);
		if (id == null) return null;
		String saltById = accountDao.getSaltById(id);
		if (saltById == null || saltById.isEmpty()) return null;
		Boolean login = accountDao.login(id, SignUtil.signing(password, saltById));
		if (Boolean.TRUE.equals(login)) {
			User user = userDao.queryByIdOrName(null, name);
			afterLogin(user);
			return user;
		}
		return null;
	}
	
	private User login(Long id, String password) {
		String saltById = accountDao.getSaltById(id);
		if (saltById == null || saltById.isEmpty()) return null;
		
		Boolean login = accountDao.login(id, SignUtil.signing(password, saltById));
		if (Boolean.TRUE.equals(login)) {
			User user = userDao.queryByIdOrName(id, null);
			afterLogin(user);
			return user;
		}
		return null;
	}
	
	// 一个钩子函数。
	private void afterLogin(User user) {
		ServletRequestAttributes requestAttributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return;
		}
		HttpServletResponse response = requestAttributes.getResponse();
		if (response == null) return;
		String format = String.format(pattern, user.getId());
		String token = redisTemplate.opsForValue().get(format);
		if (!StringUtils.hasText(token)) {
			token = SignUtil.signing();
			redisTemplate.opsForValue().set(format, token);
			redisTemplate
					.opsForList()
					.leftPushAll(token, user.getPermissions(), String.valueOf(user.getId()));
		}
		redisTemplate.expire(token, 2, TimeUnit.HOURS);
		redisTemplate.expire(format, 2, TimeUnit.HOURS);
		response.setHeader(TOKEN_RES_KEY, token);
	}
	
	@Override
	public Long registry(String name, String password, String permission) {
		String salt = SignUtil.obfuscate(Math.max(5, Math.abs(name.length() - 20)));
		return transactionTemplate.execute(status -> {
			try {
				UserAccount account = UserAccount.builder()
												 .salt(salt)
												 .password(SignUtil.signing(password, salt))
												 .build();
				// 创建账户信息
				Boolean id = accountDao.registry(account);
				if (id != Boolean.TRUE || account.getId() == null) {
					status.setRollbackOnly();
					return null;
				}
				// 注册账户信息
				Assert.isTrue(accountDao.registryUser(account.getId(), name, permission) && accountDao.registryCard(account.getId()), "");
				return account.getId();
			} catch (Exception e) {
				status.setRollbackOnly();
			}
			return null;
		});
	}
	
	@Override
	public void logout(Long id) {
		String key = String.format(pattern, id);
		String token = redisTemplate.opsForValue().get(key);
		if (token != null) {
			redisTemplate.delete(token);
		}
		redisTemplate.delete(key);
	}
	
	@Override
	public List<Backer> query(PageInfo pageInfo, Boolean isNormal, Boolean isBlack,
							  Long searchId) {
		return userDao.query(pageInfo, isNormal, isBlack, searchId);
	}
	
	@Override
	public Integer query_size(PageInfo pageInfo, Boolean isNormal, Boolean isBlack,
							  Long searchId) {
		return userDao.query_size(pageInfo, isNormal, isBlack, searchId);
	}
}
