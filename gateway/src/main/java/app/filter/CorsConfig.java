package app.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

@Configuration
public class CorsConfig {
	private final String[] origins = {
			"http://localhost",
			"http://127.0.0.1",
			"http://mauguin",
											 "https://lbrary.ddnsto.com",
			"http://192.168.100.139"};
	private final String[] headers = {
			"bl", "Access-Control-Allow-Origin",
			"Access-Control-Allow-Headers",
			"Access-Control-Allow-Credentials",
			"Access-Control-Expose-Headers",
			"Access-Control-Allow-Methods"
	};
	private final String[] requestHeaders = {
			"lb_2", "content-type", "lb_1", "RESPONSE_KEY"
	};
	
	
	@Bean
	public CorsWebFilter corsWebFilter() {
		UrlBasedCorsConfigurationSource corsConfigurationSource =
				new UrlBasedCorsConfigurationSource(new PathPatternParser());
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of(origins));
		config.setAllowedHeaders(List.of(requestHeaders));// 设置具体请求头防止用户恶意生成内部请求头
		config.addAllowedMethod("*");
		config.setExposedHeaders(List.of(headers));
		config.addExposedHeader("*");
		corsConfigurationSource.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(corsConfigurationSource);
	}
	
}
