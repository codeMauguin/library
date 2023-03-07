package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.pojo.Book;
import app.service.AdminBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class AdminBookControllerImpl implements AdminBookController {
	private final AdminBookService adminBookService;
	
	public AdminBookControllerImpl(@Autowired AdminBookService adminBookService) {
		this.adminBookService =
				adminBookService;
	}
	
	
	@Override
	public RestResponse uploadBooks(List<Book> tasks) {
		return RestResponse.response(adminBookService.uploadBooks(tasks), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse updateBook(Book book) {
		return RestResponse.response(adminBookService.updateBooks(book), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse shelvesBooks(Long id) {
		return RestResponse.response(adminBookService.shelvesBooks(id), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse takeDownBooks(Long id) {
		return RestResponse.response(adminBookService.takeDownBooks(id), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse report() {
		return RestResponse.response(adminBookService.report(), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse batchUpload(MultipartFile file, Boolean errorStop) throws IOException {
		return adminBookService.batchUpload(file.getInputStream(),
				Boolean.TRUE.equals(errorStop));
	}
}
