package app;

import app.annotation.CheckIgnore;
import app.annotation.CheckLogin;
import app.annotation.CheckRole;
import app.exception.NoPermission;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unchecked")
public class PermissionInterceptors implements HandlerInterceptor {
	private static final Class<? extends Annotation>[] ANNOTATIONS
			= new Class[]{CheckIgnore.class, CheckLogin.class,
			CheckRole.class};
	private static final ConcurrentMap<HandlerMethod, List<Annotation>> CACHE =
			new ConcurrentHashMap<>(255);
	
	/**
	 * 如果方法被@Permission注解，则检查用户是否有访问该方法的权限
	 * Todo: 2023/2/27 权限码现在只有一个 and不起效果
	 *
	 * @param request  请求对象
	 * @param response 响应对象用于将结果返回给客户端。
	 * @param handler  处理请求的对象。
	 * @return 一个布尔值。
	 */
	@Override
	
	public boolean preHandle(@Nonnull HttpServletRequest request,
							 @Nonnull HttpServletResponse response,
							 @Nonnull Object handler) throws
													  Exception {
		if (handler instanceof HandlerMethod method) {
			List<Annotation> classes = CACHE.get(method);
			if (classes == null) {
				classes = resolve(method);
				CACHE.put(method, classes);
			}
			if (classes.isEmpty()) {
				// 说明需要被忽略
				return true;
			}
			if (classes.stream().allMatch(annotation -> check(annotation, request))) {
				return true;
			} else {
				throw new NoPermission("no permission visit this server!!!");
			}
		}
		return true;
	}
	
	private List<Annotation> resolve(HandlerMethod method) {
		List<Annotation> classes = new ArrayList<>();
		Class<?> beanType = method.getBeanType();
		for (final Class<? extends Annotation> annotation : ANNOTATIONS) {
			Annotation methodAnnotation;
			methodAnnotation =
					((methodAnnotation = AnnotationUtils.findAnnotation(method.getMethod(),
							annotation)) == null ?
							 AnnotationUtils.findAnnotation(beanType, annotation) :
							 methodAnnotation);
			
			if (methodAnnotation != null) {
				if (methodAnnotation instanceof CheckIgnore) {
					return List.of();
				}
				classes.add(methodAnnotation);
			}
		}
		return classes;
	}
	
	private Boolean check(Annotation annotation, HttpServletRequest request) {
		String id = request.getHeader("proxy-id");
		String rule = request.getHeader("proxy-rule");
		if (annotation instanceof CheckLogin) {
			return StringUtils.hasText(id) && StringUtils.hasText(rule);
		}
		if (annotation instanceof CheckRole role) {
			return role.mode().equals(CheckRole.Mode.and) ?
						   Arrays.stream(role.value()).allMatch(v -> v.equals(rule)) : Arrays
																							   .asList(role.value())
																							   .contains(rule);
		}
		return true;
	}
	
}
