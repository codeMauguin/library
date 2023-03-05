package app.dao;

import app.pojo.AdminBorrowedTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pojo.PageInfo;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminOrderDao {
	List<AdminBorrowedTable> queryAll(@Param("pageInfo") PageInfo pageInfo);
	
	Integer queryAll_Size();
	
	AdminBorrowedTable getOne(@Param("id") Long id);
	
	/**
	 * Get the number of books borrowed by a user.
	 *
	 * @param id The id of the book
	 * @return The number of books borrowed by a user.
	 */
	Integer getBorrowed(@Param("id") Long id);
	
	Boolean updateBorrowed(@Param("id") Long id, @Param("state") Integer state,
						   @Param("returnTime") LocalDateTime returnTime);
	
	
}
