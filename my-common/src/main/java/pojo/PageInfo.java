package pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@Data
public class PageInfo implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -6337180262159384080L;
	@NotNull
	private Integer offset;
	private @NotNull Integer pageSize;
	private Integer size;
	private Object id;
	
}
