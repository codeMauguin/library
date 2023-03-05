package app.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReaderCount implements Serializable {
	@Serial
	private static final long serialVersionUID = -7172511028084046024L;
	private Integer borrowed;// 当前借阅数
	private BorrowedTable returnTime;// 最接近还书的时间
	private Integer timer;// 超时借阅数
	private BorrowedTable timeout;// 超时时间
	private Integer history;// 历史借阅数
	
}
