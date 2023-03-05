package app.dao;

import app.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalculateTotalDao {
	TodayReturn getReturnCount();
	
	TodayBorrowed getBorrowedCount();
	
	TotalOrder getTotalOrder();
	
	List<StatisticStatus> query_statistic_time(@Param("year") int year);
	
	List<StatisticStatus> query_statistic_return(@Param("year") int year);
	
	/**
	 * It returns the amount books borrowed by a reader.
	 *
	 * @param query The query parameter is the value that will be passed to the method.
	 * @return The amount books borrowed by a reader.
	 */
	Integer getReaderBorrowed(@Param("query") Long query);
	
	/**
	 * “获取查询的超时时间。”
	 *
	 * @param query 要执行的查询。
	 * @return 超过指定时间未签到的所有读者的列表。
	 * @Param 注解用于指定查询中参数的名称
	 */
	Integer getReaderTimeout(@Param("query") Long query);
	
	BorrowedTable getTheMostRecentBorrowingTime(@Param("query") Long query);
	
	BorrowedTable getTheMostRecentTimeOutTime(@Param("query") Long query);
	
	Integer getReaderHistoryBorrowed(@Param("query") Long query);
}
