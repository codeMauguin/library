package app.views;

import Message.RestResponse;
import app.annotation.CheckRole;
import app.annotation.JsonResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;

@Validated
public interface UserController {
	
	/**
	 * 它返回具有给定 ID 的卡片。
	 *
	 * @param id 你要获取的卡的id。
	 * @return 休息反应
	 */
	@GetMapping("/v2/card")
	@CheckRole(value = {"inject", "reader"}, mode = CheckRole.Mode.or)
	RestResponse getCard(@NotNull Long id);
	
	@PostMapping("/v2/user/revise")
	@CheckRole(value = {"admin", "reader"}, mode = CheckRole.Mode.or)
	RestResponse revise(@JsonResponse String name,
						@JsonResponse String password,
						@RequestHeader("proxy-id") Long id);
	
	@GetMapping("/code")
	void code(@NotNull(message = "用户名不能为空") String username, HttpServletResponse response) throws
																								  IOException;
	
	@GetMapping("/code/valid")
	RestResponse code_valid(@NotNull String username,@NotNull String code);
}
