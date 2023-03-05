package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.annotation.JsonResponse;
import app.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pojo.PageInfo;

@RestController
public class BookControllerImpl implements BookController {
	
	private final BookService bookService;
	
	public BookControllerImpl(@Autowired BookService bookService) {
		this.bookService = bookService;
	}
	
	@Override
	public RestResponse bookTotal(
			String rule
								 ) {
		return RestResponse.response(bookService.getBookSize(!"admin".equals(rule)),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse book(Long bookId) {
		return RestResponse
					   .response(bookService.getBook(bookId),
							   
							   ResponseCode.SUCCESS);
		
	}
	
	@Override
	public RestResponse queryBookName(String keyword, String condition) {
		return RestResponse.response(bookService.queryBookName(keyword, condition),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse queryBooks(String search, String mode) {
		return RestResponse.response(bookService.queryBook(search, mode), ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse Books(String rule,
							  @JsonResponse PageInfo pageInfo) {
		return RestResponse.response(bookService.getBooks(!"admin".equals(rule), pageInfo)
				, ResponseCode.SUCCESS);
	}
	
}
