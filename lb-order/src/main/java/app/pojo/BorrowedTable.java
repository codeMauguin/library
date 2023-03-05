package app.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BorrowedTable implements Serializable {
	
	
	@Serial private static final long serialVersionUID = 718064473659697858L;
	private Long id;
	private Long orderId;
	private Long bookId;
	private LocalDateTime createTime;
	private LocalDateTime returnTime;
	private Integer status;
	private Integer renewal;
}
