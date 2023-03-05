package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserCard implements Serializable {
	@Serial private static final long serialVersionUID = 2669581477013553140L;
	private Long id;
	private Integer total;
	private Integer borrowed;
	private Integer timeout;
}
