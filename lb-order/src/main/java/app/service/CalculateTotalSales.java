package app.service;


import app.pojo.ReaderCount;
import app.pojo.TodayBorrowed;
import app.pojo.TodayReturn;
import app.pojo.TotalOrder;

import java.util.Map;

public interface CalculateTotalSales {
	TodayBorrowed getBorrowedCount();
	
	TodayReturn getReturnCount();
	
	TotalOrder getTotalOrder();
	
	Map<String, int[]> geostationary(int year);
	
	
	ReaderCount getCount(Long id);
}
