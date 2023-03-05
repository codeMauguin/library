package app.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Token {
	/**
	 * It returns the value of the `proxy-id` header from the current request, or `null` if there
	 * is no
	 * current request
	 *
	 * @return The current user's ID.
	 */
	public static Long getCurrentId() {
		ServletRequestAttributes requestAttributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) return null;
		String header = requestAttributes.getRequest().getHeader("proxy-id");
		return header == null ? null : Long.valueOf(header);
	}
}
