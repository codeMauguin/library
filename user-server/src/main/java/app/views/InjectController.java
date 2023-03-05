package app.views;

import app.annotation.CheckRole;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.user;

@Validated
@CheckRole("inject")
public interface InjectController {
	/**
	 * 内部查询用户信息
	 *
	 * @param id 用户id
	 * @return 用户信息
	 */
	@GetMapping("/query/user")
	user query(@NotNull Long id);
	
	@GetMapping("/inject/cardId")
	Long queryCardId(@NotNull Long id);
	
	@GetMapping(value = "/inject/user/id")
	user queryUser(@RequestParam("cardId") Long cardId);
	
	@PostMapping(value = "/inject/borrowed/deduction/{userId}/{size}")
	Boolean deductionOfBorrowed(@NotNull @PathVariable Integer size,
								@NotNull @PathVariable Long userId);
}
