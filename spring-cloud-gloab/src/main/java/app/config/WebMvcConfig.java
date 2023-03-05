package app.config;

import app.PermissionInterceptors;
import app.RequestBodyAdvice;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@Import(RequestBodyAdvice.class)
public class WebMvcConfig implements WebMvcConfigurer {
	private final RequestBodyAdvice requestBodyAdvice;
	
	public WebMvcConfig(@Autowired RequestBodyAdvice requestBodyAdvice) {
		this.requestBodyAdvice =
				requestBodyAdvice;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new PermissionInterceptors()).addPathPatterns("/**");
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(requestBodyAdvice);
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return jacksonObjectMapperBuilder ->
					   jacksonObjectMapperBuilder
							   
							   .serializerByType(LocalDateTime.class,
									   new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(
											   "yyyy-MM-dd HH:mm:ss")))
							   .serializerByType(Long.class,
									   ToStringSerializer.instance)
							   .serializerByType(long.class
									   , ToStringSerializer.instance)
							   .serializerByType(Long.TYPE,
									   ToStringSerializer.instance);
	}
}
