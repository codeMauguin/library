package app.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CommentRequest implements Serializable {
	
	@Serial private static final long serialVersionUID = -3294749946153288693L;
	@NotNull private Long userId;
	@NotBlank private String content;
	@NotNull private Long bookId;
	private Long parentId;
}
