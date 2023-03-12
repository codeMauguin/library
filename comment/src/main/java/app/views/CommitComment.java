package app.views;

import Message.RestResponse;
import app.annotation.CheckLogin;
import app.annotation.CheckRole;
import app.pojo.CommentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@CheckLogin
@CheckRole(value = {"reader","admin"},mode =  CheckRole.Mode.or)
public interface CommitComment {
	@PostMapping("/comment/commit")
	RestResponse commit(@Valid @RequestBody CommentRequest comment);
	
	@PatchMapping("/delete/{id}/{userId}")
	RestResponse deleteComment(@NotNull @PathVariable Long id, @NotNull @PathVariable Long userId);
}
