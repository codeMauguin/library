package pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
public class PageData<T> implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -7688543942683975939L;
	private Integer size;
	private List<T> value;
	
	
	/**
	 * > The function is used to query data from the database and return the data to the front end
	 *
	 * @param pageInfo The page information object passed in by the front end
	 * @param function The function that queries the database, which is the function that queries
	 *                   the
	 *                 database.
	 * @param pageSize The number of data in the database
	 * @return A PageData object with a list of T objects.
	 */
	public static <T> PageData<T> response(PageInfo pageInfo,
										   Function<PageInfo, List<T>> function,
										   Supplier<Integer> pageSize) {
		PageData<T> pageData = new PageData<>();
		// 当前台不知道当前数据长度时查询
		if (pageInfo.getSize() == null || pageInfo.getSize() < 0) {
			pageData.setSize(pageSize.get());
		}
		// 当没有页面数据不需要进行数据查询
		if (pageInfo.getSize() == 0) {
			pageData.setValue(List.of());
			return pageData;
		}
		// 从数据库查询数据
		List<T> apply = function.apply(pageInfo);
		pageData.setValue(apply);
		return pageData;
	}
}
