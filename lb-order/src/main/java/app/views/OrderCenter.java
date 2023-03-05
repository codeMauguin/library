package app.views;

import Message.RestResponse;
import app.annotation.CheckRole;
import app.annotation.JsonResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pojo.PageInfo;

import java.util.List;

@Validated
@CheckRole(value = "reader")
public interface OrderCenter {
	
	@PostMapping("/create")
	RestResponse createOrder(
			@NotNull @RequestHeader("proxy-id") Long id,
			@RequestBody
			@NotEmpty List<Long> bookIds);
	
	@PostMapping("/commit")
	@Validated
	RestResponse commit(@NotNull @RequestHeader("proxy-id") Long id,
						@NotNull @JsonResponse(once = true) Long orderId);
	
	@PostMapping("/getOrders")
	RestResponse getOrders(@Valid @RequestBody PageInfo pageInfo, @NotNull
	@RequestHeader("proxy-id") Long id);
	
	@PostMapping("/getOrders/condition")
	RestResponse getOrdersByCondition(@JsonResponse List<Long> timer,
									  @JsonResponse Long orderId,
									  @JsonResponse Integer status,
									  @RequestHeader("proxy-id") Long id);
	
	@PostMapping("/book/borrowed")
	RestResponse getBorrowed(
			@RequestHeader("proxy-id") Long id,
			@NotNull @JsonResponse PageInfo pageInfo,
			@JsonResponse List<Integer> status
							);
	
	@PutMapping("/book/renewal")
	RestResponse renewal(@RequestHeader("proxy-id") Long id, @NotNull @RequestBody Long cid);
	
	/**
	 * 返回书籍
	 *
	 * @param id     借阅id
	 * @param userId 用户ID
	 * @return 返回结果
	 */
	@PatchMapping("/returnBook")
	@JsonResponse(once = true)
	RestResponse returnBook(@NotNull Long id,
							@RequestHeader("proxy-id") Long userId);
	
	@GetMapping("/reader/borrowed")
	RestResponse getHasBorrowed(@NotNull(message = "身份不合法") @RequestHeader("proxy-id") Long userId);
	
	/**
	 * 用户统计接口
	 *
	 * @param userId 用户id
	 * @return 1
	 */
	@GetMapping("/reader/statistics")
	RestResponse statistics(@NotNull(message = "身份不合法") @RequestHeader("proxy-id") Long userId);
	
	
}
