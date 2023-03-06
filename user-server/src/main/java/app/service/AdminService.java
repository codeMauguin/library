package app.service;

import app.pojo.Admin_user;
import app.pojo.Backer;
import pojo.PageData;
import pojo.PageInfo;

public interface AdminService {
	PageData<Admin_user> getUsers(PageInfo pageInfo);
	
	Boolean updateUserTotal(Long id, Integer total);
	
	Boolean pullIntoTheBlacklist(Long id);
	
	PageData<Backer> getUsersBack(PageInfo pageInfo, Boolean isNormal,
								  Boolean isBlack,
								  Long searchId);
	
	Boolean restoreInto(Long id);
}
