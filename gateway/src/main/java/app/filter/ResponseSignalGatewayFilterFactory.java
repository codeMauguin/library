package app.filter;

import app.utils.AESUtil;
import jakarta.annotation.Nonnull;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static app.filter.TerminalSignalTokenGatewayFilterFactory.KEY;

@Component
public class ResponseSignalGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory implements
																							  GatewayFilter, Ordered {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpResponse response = exchange.getResponse();
		DataBufferFactory dataBufferFactory = response.bufferFactory();
		ServerHttpResponseDecorator serverHttpResponseDecorator =
				new ServerHttpResponseDecorator(response) {
					
					@Override
					@Nonnull
					public Mono<Void> writeWith(@Nonnull Publisher<? extends DataBuffer> body) {
						if ((body instanceof Flux<? extends DataBuffer> next)) {
							return super.writeWith(next.buffer().map(dataBuffers -> {
								DataBufferFactory factory = new DefaultDataBufferFactory();
								DataBuffer join = factory.join(dataBuffers);
								byte[] content = new byte[join.readableByteCount()];
								join.read(content);
								DataBufferUtils.release(join);
								if (content.length == 0) return dataBufferFactory.wrap(content);
								List<String> strings =
										response.getHeaders().get(KEY);
								response.getHeaders().remove(KEY);
								if (strings == null || strings.isEmpty()) {
									super.getHeaders().setContentLength(content.length);
									return dataBufferFactory.wrap(content);
								}
								byte[] encode = AESUtil.encryptECB(content,
										strings.get(0));
								super.getHeaders().setContentLength(encode.length);
								
								// 验证生成结果正确性
								return dataBufferFactory.wrap(Optional
																	  .of(encode)
																	  .orElse(new byte[0]));
							}));
						}
						return super.writeWith(body);
					}
				};
		
		return chain.filter(exchange.mutate().response(serverHttpResponseDecorator).build());
	}
	
	@Override
	public GatewayFilter apply(NameValueConfig config) {
		return this;
	}
	
	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}