package app.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class Admin_user extends User implements Serializable {
	@Serial
	private static final long serialVersionUID = 1361978392181652585L;
	private Integer borrowed;
	private Long cardId;
	private Integer total;
}
