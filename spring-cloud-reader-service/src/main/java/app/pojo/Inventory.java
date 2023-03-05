package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Inventory implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -8351432857604312073L;
	private Long bookId;
	private Integer total;
	private Integer borrowed;
}
