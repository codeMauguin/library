package app.views;


import Message.RestResponse;
import app.annotation.CheckRole;
import app.pojo.Book;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Validated
@CheckRole("admin")
public interface AdminBookController {
	@PostMapping("/api/book/task")
	RestResponse uploadBooks(@NotEmpty @RequestBody List<Book> tasks);
	
	@PatchMapping("/api/book/update")
	RestResponse updateBook(@Validated(AdminBookController.class) @NotNull(groups =
																				   AdminBookController.class) @RequestBody Book book);
	
	@PatchMapping("/api/book/shelves")
	RestResponse shelvesBooks(@NotNull @RequestBody Long id);
	
	@PatchMapping("/api/book/takeDown")
	RestResponse takeDownBooks(@NotNull @RequestBody Long id);
	
	@GetMapping("/api/report")
	RestResponse report();
}
