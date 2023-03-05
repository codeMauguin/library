package pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class user implements Serializable {
	
	@Serial private static final long serialVersionUID = 6890946621775908912L;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String name;
	private String image;
}
