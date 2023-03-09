package app.views;

import Message.RestResponse;
import app.annotation.CheckLogin;
import app.annotation.CheckRole;
import app.annotation.JsonResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
public interface AccountController {
	/**
	 * 此函数使用给定的 ID、名称和密码登录用户，并返回一个 RestResponse 对象。
	 *
	 * @param id       用户的 ID。
	 * @param name     用户的名称。
	 * @param password 用户的密码。
	 * @return 一个 RestResponse 对象。
	 */
	@PostMapping("/v2/login")
	@JsonResponse
	RestResponse login(Long id,  String name,@NotNull String card,
					   @NotNull(message = "密码为空") String password);
	
	/**
	 * 注册用户的功能。
	 *
	 * @param name     用户名
	 * @param password 用户密码
	 * @return 一个 RestResponse 对象。
	 */
	@JsonResponse
	@PostMapping("/registry")
	RestResponse registry(@NotBlank(message = "用户名不能为空") String name,
						  @NotBlank(message = "密码不能为空") String password);
	
	/**
	 * 注册用户的功能。
	 *
	 * @param name       用户名
	 * @param password   用户密码
	 * @param permission 用户的权限，即权限组的名称。
	 * @return 一个 RestResponse 对象。
	 */
	@JsonResponse
	@PostMapping("/v2/registry")
	@CheckRole("admin")
	RestResponse registry(@NotBlank(message = "用户名不能为空") String name,
						  @NotBlank(message = "密码不能为空") String password,
						  @NotBlank String permission);
	
	@PatchMapping("/v2/logout")
	@CheckLogin
	@JsonResponse
	RestResponse logout(@NotNull(message = "下线用户未知") @RequestBody Long id);
	
}
