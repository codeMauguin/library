package app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class UserAccount implements Serializable {
	@Serial
	private static final long serialVersionUID = 1284371637838438487L;
	private Long id;
	@JsonIgnore
	private String salt;
	private String password;
	
}
