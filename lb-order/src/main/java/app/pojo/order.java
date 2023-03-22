package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class order implements Serializable {
	@Serial
	private static final long serialVersionUID = 3208219343633978480L;
	private Long id;
	private LocalDateTime createTime;
	private LocalDateTime endTime;
	private Integer status;
	private String reason;
	
}
