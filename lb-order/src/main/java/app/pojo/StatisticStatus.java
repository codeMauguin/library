package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class StatisticStatus implements Serializable {
	@Serial
	private static final long serialVersionUID = 3461160301079559966L;
	private Integer month;
	private Integer borrowed;
}
