package app.service;

import app.pojo.AdminBorrowedTable;
import pojo.PageData;
import pojo.PageInfo;

public interface AdminService {
	Integer getBorrowed(Long id);
	
	PageData<AdminBorrowedTable> getAllBorrowed(PageInfo page);
	
	
	Boolean flagBorrowed(Long id, Integer state);
	
}
