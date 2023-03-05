package app.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TodayReturn implements Serializable {
	
	@Serial private static final long serialVersionUID = 7211861054215733594L;
	private Integer count;// 今天归还
	private Integer yesterday;// 昨天的归还数
}
