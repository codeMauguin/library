package app.pojo;

import app.views.AdminBookController;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Book implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -3094463850364701616L;
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull(groups = AdminBookController.class)
	private long id;
	@NotNull
	private String name;
	@NotNull
	private String category;
	private String author;
	@NotNull
	private String press;
	private String info;
	private String image;
	private Inventory inventory;
	private Integer state;
	@JsonFormat(pattern = "yyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime time;
	
}
