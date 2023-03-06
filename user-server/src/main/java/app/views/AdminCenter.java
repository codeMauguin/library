package app.views;

import Message.RestResponse;
import app.annotation.CheckRole;
import app.annotation.JsonResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pojo.PageInfo;

@Validated
@CheckRole("admin")
public interface AdminCenter {
	@GetMapping("/account")
	RestResponse getUsers(@NotNull PageInfo pageInfo);
	
	@GetMapping("/account/all")
	RestResponse getUsersBack(@NotNull PageInfo pageInfo,
							  @RequestParam(defaultValue = "true") Boolean isNormal,
							  @RequestParam(defaultValue = "true") Boolean isBlack,
							  Long searchId);
	
	@PatchMapping("/api/user/total")
	@JsonResponse
	RestResponse update_user_total(@NotNull Long id, @NotNull Integer total);
	
	@PutMapping("/api/pullIntoBackList")
	RestResponse pullIntoTheBlacklist(@NotNull @RequestBody Long id);
	
	@PutMapping("/api/restoreInto")
	RestResponse restoreInto(@NotNull @RequestBody Long id);
}
