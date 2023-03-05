package app.views;


import app.annotation.CheckRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@CheckRole("inject")
public interface InjectController {
	@PostMapping("inject/stocks")
	List<Integer> getBookStock(@RequestBody List<Long> bookIds);
	
	@PostMapping(value = "/inject/inventory/deduction")
	Boolean deductionOfInventory(@RequestBody @NotEmpty List<Long> bookIds);
	
	@PostMapping(value = "/inject/inventory/increment")
	Boolean incrementOfInventory(@RequestBody @NotEmpty List<Long> bookIds);
	
	@GetMapping(value = "/inject/book/name")
	String queryBookName(@NotNull @RequestParam("bookId") Long bookId);
}
