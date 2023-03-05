package app.filter;

import app.utils.RsaUtil;
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

import java.util.Optional;

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
					public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
						if ((body instanceof Flux<? extends DataBuffer> next)) {
							return super.writeWith(next.buffer().map(dataBuffers -> {
								DataBufferFactory factory = new DefaultDataBufferFactory();
								DataBuffer join = factory.join(dataBuffers);
								byte[] content = new byte[join.readableByteCount()];
								join.read(content);
								DataBufferUtils.release(join);
								if (content.length == 0) return dataBufferFactory.wrap(content);
								// 进行加密编码
								byte[] encode = RsaUtil.encode(RsaUtil.base64.encode(content),
										TerminalSignalTokenGatewayFilterFactory.PUBLIC_KEY);
								if (encode == null) {
									super.getHeaders().setContentLength(content.length);
									return dataBufferFactory.wrap(content);
								}
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
