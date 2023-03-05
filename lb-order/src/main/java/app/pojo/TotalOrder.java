package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TotalOrder implements Serializable {
	@Serial
	private static final long serialVersionUID = 4901447216795621555L;
	private Integer count;
	private Integer timeout;
}
