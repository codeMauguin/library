package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TodayBorrowed implements Serializable {
	@Serial
	private static final long serialVersionUID = 3461160301079559966L;
	private Integer count;// 今天借阅
	private Integer yesterday;// 昨天的借阅数
}
