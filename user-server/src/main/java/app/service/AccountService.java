package app.service;

import app.pojo.Backer;
import app.pojo.User;
import pojo.PageInfo;

import java.util.List;

public interface AccountService {
	/**
	 * 此函数接受一个 Long、一个 String 和一个 String，并返回一个布尔值。
	 *
	 * @param id       用户的 ID。
	 * @param name     用户的名称。
	 * @param password 用户的密码。
	 * @return 一个布尔值。
	 */
	User login(Long id, String name, String password);
	
	
	/**
	 * > 该函数用于注册新用户
	 *
	 * @param name     用户的名称。
	 * @param password 用户的密码。
	 * @return 一个布尔值。
	 */
	default Long registry(String name, String password, String permission) {
		return null;
	}
	
	void logout(Long id);
	
	List<Backer> query(PageInfo pageInfo, Integer status);
	
	Integer query_size(PageInfo pageInfo, Integer status);
	
}
