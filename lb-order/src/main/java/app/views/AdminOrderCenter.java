package app.views;

import Message.RestResponse;
import app.annotation.CheckRole;
import app.annotation.JsonResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pojo.PageInfo;

@CheckRole("admin")
@Validated
public interface AdminOrderCenter {
	@GetMapping("/borrowed/all")
	RestResponse getAllBorrowed(@NotNull PageInfo page);
	
	@PatchMapping("/api/book/borrowed")
	@JsonResponse
	RestResponse flag_borrowed(@NotNull Long id, @NotNull Integer state);
	
	@GetMapping("/api/statistic")
	RestResponse statistic();
	
	@GetMapping("/api/statistic/year/{year}")
	RestResponse statistic_year(@PathVariable @NotNull Integer year);
}
