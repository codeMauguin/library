package app.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
	String[] value();
	
	Mode mode() default Mode.or;
	
	enum Mode {
		or, and
	}
}
