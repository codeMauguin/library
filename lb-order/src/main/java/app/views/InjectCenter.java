package app.views;

import app.annotation.CheckRole;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CheckRole(
		"inject"
)
public sealed interface InjectCenter permits InjectCenterImpl {
	@GetMapping(value = "/inject/borrowed")
	Integer getBorrowed(@NotNull @RequestParam("id") Long id);
}
