package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AdminBorrowedTable implements Serializable {
	
	@Serial private static final long serialVersionUID = 555027688568662979L;
	private Long id;
	private Long cardId;
	private Long userId;
	private Long bookId;
	private String bookName;
	private String username;
	private Integer status;
}
