package app.service.impl;

import app.dao.AdminDao;
import app.pojo.Admin_user;
import app.pojo.Backer;
import app.service.AccountService;
import app.service.AdminService;
import com.alibaba.nacos.shaded.com.google.common.base.Function;
import com.alibaba.nacos.shaded.com.google.common.base.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pojo.PageData;
import pojo.PageInfo;

import java.sql.SQLException;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
	private final AdminDao adminDao;
	private final Function<PageInfo, List<Admin_user>> getAllUser;
	private final Supplier<Integer> getAllUserSize;
	private final AccountService accountService;
	
	public AdminServiceImpl(@Autowired AdminDao adminDao,
							@Autowired AccountService accountService) {
		this.adminDao = adminDao;
		this.getAllUser = adminDao::getAllUser;
		this.getAllUserSize = adminDao::getAllUser_size;
		this.accountService = accountService;
	}
	
	@Override
	public PageData<Admin_user> getUsers(PageInfo pageInfo) {
		return PageData.response(pageInfo,
				getAllUser,
				getAllUserSize);
	}
	
	@Override
	public Boolean updateUserTotal(Long id, Integer total) {
		return adminDao.updateUserTotal(id, total);
	}
	
	@Override
	@Transactional(rollbackFor = {SQLException.class, Exception.class,
			IllegalStateException.class})
	public Boolean pullIntoTheBlacklist(Long id) {
		Assert.state(adminDao.pull(id, 1), "拉黑失败");
		Assert.state(adminDao.updateUserRole(id, "NOT_SAFE"), "权限纠正失败");
		accountService.logout(id);
		return true;
	}
	
	@Override
	public PageData<Backer> getUsersBack(PageInfo pageInfo, Integer status) {
		return PageData
					   .response(pageInfo, (page) -> accountService.query(page, status),
							   () -> accountService.query_size(pageInfo, status));
	}
	
	@Override
	@Transactional(rollbackFor = {SQLException.class, Exception.class,
			IllegalStateException.class})
	public Boolean restoreInto(Long id) {
		Assert.state(adminDao.pull(id, 0), "恢复失败");
		Assert.state(adminDao.updateUserRole(id, "reader"), "权限纠正失败");
		accountService.logout(id);
		return true;
	}
}
