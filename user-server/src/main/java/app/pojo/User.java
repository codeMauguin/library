package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class User implements Serializable {
	@Serial
	private static final long serialVersionUID = 7604781635815896574L;
	private Long id;
	private String name;
	private String permissions;
}
