package app.dao;

import app.pojo.Backer;
import app.pojo.User;
import app.pojo.UserCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pojo.PageInfo;
import pojo.user;

import java.util.List;

@Mapper
public interface UserDao {
	/**
	 * 按 ID 或名称查询用户。
	 *
	 * @param id   用户的 ID。
	 * @param name 参数的名称。
	 * @return 用户对象
	 */
	User queryByIdOrName(@Param("id") Long id, @Param("name") String name);
	
	user queryById(@Param("id") Long id);
	
	UserCard queryCardById(@Param("id") Long id);
	
	Long queryCardId(@Param("id") Long id);
	
	/**
	 * > Deduct the number of books borrowed by the user
	 *
	 * @param size   The number of books to be borrowed
	 * @param userId user ID
	 * @return Boolean
	 */
	Boolean deductionOfBorrowed(@Param("size") Integer size, @Param("userId") Long userId);
	
	user queryUser(@Param("cardId") Long cardId);
	
	Boolean revise(@Param("name") String name, @Param("id") Long id);
	
	List<Backer> query(@Param("pageInfo") PageInfo pageInfo, @Param("status") Integer status);
	
	Integer query_size(@Param("pageInfo") PageInfo pageInfo, @Param("status") Integer status);
}
