package app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pojo.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Comment implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 3284931436124532318L;
	private Long id;
	private Long bookId;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer child_size;
	private Long parentId;
	private Long rootId;
	@JsonIgnore
	private Long user_id;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private user user;
	
	private String content;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime timestamp;
	private Integer liked;
	private Integer unLike;
	private Boolean isLiked;
	private Boolean isUnLiked;
}
