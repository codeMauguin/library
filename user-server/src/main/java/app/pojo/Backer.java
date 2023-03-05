package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Backer implements Serializable {
	@Serial
	private static final long serialVersionUID = -3433151546285272986L;
	private Long id;
	private String name;
	private Integer status;
}
