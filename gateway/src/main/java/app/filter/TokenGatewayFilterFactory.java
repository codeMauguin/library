package app.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
	private final static String EMPTY_VALUE = "__.__.__.__.__";
	private final RedisTemplate<String, String> redisTemplate;
	
	public TokenGatewayFilterFactory(@Autowired RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	@Override
	public GatewayFilter apply(NameValueConfig config) {
		return ((exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			List<String> strings = request.getHeaders().get(config.getValue());
			String id = EMPTY_VALUE;
			String rule = EMPTY_VALUE;
			A:
			{
				// 获取用户id 权限
				if (strings == null || strings.isEmpty()) break A;
				List<String> range = redisTemplate.opsForList().range(strings.get(0), 0, 2);
				if (range == null || range.size() < 2) break A;
				id = range.get(0);
				rule = range.get(1);
			}
			ServerHttpRequest builder = request
												.mutate()
												.header("proxy-id", id)
												.header("proxy-rule", rule)
												.build();
			return chain.filter(exchange.mutate().request(builder).build());
		});
	}
	
}
