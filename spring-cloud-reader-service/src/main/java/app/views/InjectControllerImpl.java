package app.views;

import app.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InjectControllerImpl implements InjectController {
	private final BookService bookService;
	
	
	public InjectControllerImpl(@Autowired BookService bookService) {
		this.bookService =
				bookService;
		;
	}
	
	@Override
	public List<Integer> getBookStock(List<Long> bookIds) {
		return bookService.getBookStock(bookIds);
	}
	
	@Override
	public Boolean deductionOfInventory(List<Long> bookIds) {
		return bookService.deductionOfInventory(bookIds);
		
	}
	
	@Override
	public Boolean incrementOfInventory(List<Long> bookIds) {
		return bookService.incrementOfInventory(bookIds);
	}
	
	@Override
	public String queryBookName(Long bookId) {
		return bookService.queryBookName(bookId);
	}
}
