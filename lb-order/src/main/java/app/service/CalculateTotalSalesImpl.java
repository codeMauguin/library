package app.service;

import app.dao.CalculateTotalDao;
import app.inject.InjectService;
import app.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CalculateTotalSalesImpl implements CalculateTotalSales {
	
	private final CalculateTotalDao calculateTotalDao;
	private final InjectService injectService;
	
	public CalculateTotalSalesImpl(@Autowired CalculateTotalDao calculateTotalDao,
								   @Autowired InjectService injectService) {
		
		this.calculateTotalDao = calculateTotalDao;
		this.injectService = injectService;
	}
	
	@Override
	public TodayBorrowed getBorrowedCount() {
		return calculateTotalDao.getBorrowedCount();
	}
	
	@Override
	public TodayReturn getReturnCount() {
		return calculateTotalDao.getReturnCount();
	}
	
	@Override
	public TotalOrder getTotalOrder() {
		return calculateTotalDao.getTotalOrder();
	}
	
	@Override
	public Map<String, int[]> geostationary(int year) {
		List<StatisticStatus> ints = calculateTotalDao.query_statistic_time(year);
		List<StatisticStatus> statisticStatuses = calculateTotalDao.query_statistic_return(year);
		int[] borrowed = new int[12];
		ints.forEach(anInt -> borrowed[anInt.getMonth() - 1] = anInt.getBorrowed());
		int[] returnTotal = new int[12];
		statisticStatuses.forEach(statisticStatus -> returnTotal[statisticStatus.getMonth() - 1] =
															 statisticStatus.getBorrowed());
		return Map.of("borrowed", borrowed, "returnTotal", returnTotal);
	}
	
	@Override
	public ReaderCount getCount(Long userId) {
		Long query = injectService.query(userId);
		Integer readerBorrowed = calculateTotalDao.getReaderBorrowed(query);
		Integer readerHistoryBorrowed = calculateTotalDao.getReaderHistoryBorrowed(query);
		// 当前借阅数
		Integer readerTimeout = calculateTotalDao.getReaderTimeout(query);
		
		ReaderCount.ReaderCountBuilder builder =
				ReaderCount
						.builder()
						.borrowed(readerBorrowed)
						.timer(readerTimeout)
						.history(readerHistoryBorrowed);
		if (readerBorrowed != null && readerBorrowed > 0) {
			builder.returnTime(calculateTotalDao.getTheMostRecentBorrowingTime(query));
		}
		if (readerTimeout != null && readerTimeout > 0) {
			builder.timeout(calculateTotalDao.getTheMostRecentTimeOutTime(query));
		}
		return builder.build();
	}
	
}
