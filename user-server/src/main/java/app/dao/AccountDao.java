package app.dao;

import app.pojo.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountDao {
	/**
	 * 为具有给定 ID 的用户获取盐。
	 *
	 * @param id 用户的id
	 * @return 一个字符串
	 */
	@Select("SELECT salt FROM lb_account WHERE id = #{id}")
	String getSaltById(@Param("id") Long id);
	
	/**
	 * 为具有给定名称的用户获取盐。
	 *
	 * @param name 要检索其 salt 的用户的名称。
	 * @return 具有给定名称的用户的盐。
	 */
	@Select("SELECT id FROM lb_user WHERE name = #{name} ")
	Long queryId(@Param("name") String name);
	
	@Select("{call login(#{id}, #{password})}")
	Boolean login(@Param("id") Long id, @Param("password") String password);
	
	
	Boolean registry(@Param("account") UserAccount account);
	
	/**
	 * 使用给定的 ID、名称和权限注册用户。
	 *
	 * @param id         用户的 ID。
	 * @param name       用户的名称。
	 * @param permission 用户的权限级别。
	 * @return 一个布尔值。
	 */
	Boolean registryUser(@Param("id") Long id, @Param("name") String name,
						 @Param("permission") String permission);
	
	/**
	 * 该函数返回一个布尔值，并接受一个长整型值作为参数。
	 *
	 * @param id 要注册的卡的id。
	 * @return 布尔值
	 */
	Boolean registryCard(@Param("id") Long id);
	
	/**
	 * Revise the document with the given id, using the given signing.
	 *
	 * @param id      The id of the record to be updated
	 * @param signing The signing parameter is used to determine whether the data is passed to the
	 *                  front
	 *                end.
	 * @return A boolean value.
	 */
	Boolean revise(@Param("id") Long id, @Param("signing") String signing);
}
