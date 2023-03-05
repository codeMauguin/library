package app.dao;

import app.pojo.Admin_user;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pojo.PageInfo;

import java.util.List;

@Mapper
public interface AdminDao {
	List<Admin_user> getAllUser(@Param("pageInfo") PageInfo pageInfo);
	
	Integer getAllUser_size();
	
	Boolean updateUserTotal(@Param("id") Long id, @Param("total") Integer total);
	
	Boolean pull(@Param("id") Long id, @Param("status") Integer status);
	
	boolean updateUserRole(@Param("id") Long id, @Param("notSafe") String notSafe);
}
