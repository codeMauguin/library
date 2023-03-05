package app.dao;

import app.pojo.BorrowedTable;
import app.pojo.order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.PageInfo;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface QueryDao {
	List<order> getOrders(@Param("pageInfo") PageInfo pageInfo, @Param("id") Long id);
	
	@Select("SELECT COUNT(id) FROM lb_order WHERE card_id = #{query}")
	Integer getOrderCount(@Param("query") Long query);
	
	List<order> getOrdersByCondition(@Param("start") LocalDateTime start,
									 @Param("end") LocalDateTime end,
									 @Param("orderId") Long orderId,
									 @Param("status") Integer status,
									 @Param("query") Long query);
	
	List<BorrowedTable> getBorrowers(@Param("pageInfo") PageInfo pageInfo,
									 @Param("status") List<Integer> status, @Param("id") Long id);
	
	BorrowedTable getBorrowed(@Param("id") Long id, @Param("query") Long query);
	
	Integer getBorrowedSize(@Param("status") List<Integer> status, @Param("id") Long query);
	
	List<Long> getHasBorrowed(@Param("cardId") Long cardId);
	
	Boolean renewal(@Param("table") BorrowedTable table);
}
