package app.views;

import Message.RestResponse;
import app.annotation.CheckLogin;
import app.annotation.CheckRole;
import app.pojo.CommentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
@CheckLogin
@CheckRole("reader")
public interface CommitComment {
	@PostMapping("/comment/commit")
	RestResponse commit(@Valid @RequestBody CommentRequest comment);
	
	@GetMapping("/delete/{id}/{userId}")
	RestResponse deleteComment(@NotNull @PathVariable Long id, @NotNull @PathVariable Long userId);
}
