package app.views;

import Message.RestResponse;
import app.annotation.CheckRole;
import app.annotation.JsonResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import pojo.PageInfo;

@Validated
public interface BookController {
	/**
	 * > 返回数据库中的图书总数
	 *
	 * @return 一个 RestResponse 对象。
	 */
	@GetMapping("/book/size")
	@CheckRole("inject")
	RestResponse bookTotal(
			@RequestHeader(value = "proxy-rule", defaultValue = "reader") String rule
						  );
	
	@PostMapping("/v2/books")
	RestResponse Books(
			@RequestHeader(value = "proxy-rule", defaultValue = "reader") String rule,
			@NotNull @Valid @JsonResponse PageInfo pageInfo
					  );
	
	@GetMapping("/v2/book/{bookId}")
	/**
	 * This function returns a book with the given ID.
	 *
	 * @param bookId The path variable that will be used to identify the book.
	 * @return A RestResponse object.
	 */
	RestResponse book(@NotNull @PathVariable Long bookId);
	
	@GetMapping("/v2/book/search/key_word")
	RestResponse queryBookName(@NotBlank String keyword, @NotBlank String condition);
	
	@GetMapping("v2/book/search")
	RestResponse queryBooks(@NotBlank String search, @NotBlank String mode);
	
}
