package app.views;


import Message.RestResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import pojo.PageInfo;


public interface CommentController {
	@PostMapping({"/getRoots/{id}", "/getRoots/{id}/{userId}"})
	RestResponse getCommentRoots(@PathVariable Long id,
								 @PathVariable(required = false) Long userId,
								 @RequestBody PageInfo pageInfo);
	
	@GetMapping("/comment/children")
	RestResponse getCommentChildren(@RequestParam("id") Long id,
									@RequestParam(value = "userId", required = false) Long userId);
	
	@PatchMapping("/like")
	RestResponse commentLikes(
			@NotNull @RequestBody Long id,
			@NotNull @RequestHeader("proxy-id") Long userId
							 );
	
	@PatchMapping("/cancelLike")
	RestResponse commentCancelLikes(
			@NotNull @RequestBody Long id,
			@NotNull @RequestHeader("proxy-id") Long userId
								   );
	
	@PatchMapping("/unlike")
	RestResponse commentUnLikes(
			@NotNull @RequestBody Long id,
			@NotNull @RequestHeader("proxy-id") Long userId
							   );
	
}
