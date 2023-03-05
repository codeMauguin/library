package app.annotation;


import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonResponse {
	/**
	 * @implSpec 在方法上多个参数时不要填写
	 */
	String[] value() default "";
	
	boolean once() default false;
	
	String defaultValue() default "";
}
