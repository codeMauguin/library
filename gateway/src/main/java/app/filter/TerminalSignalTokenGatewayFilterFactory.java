package app.filter;

import app.utils.RsaUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nonnull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TerminalSignalTokenGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory implements
																								   GatewayFilter, Ordered {
	public final static String PRIVATE_KEY =
			"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDX3NSat9vf03azTPjmef1ZiP0" +
					"+WWWUIXOgOtlD5EHgI570d2+77z5rNft" +
					"/EuhJyXmqU9dTTrkjQsLkBb13LXIPKC8b3trX0LIlDM3vxUQXr8Ndiqw3oyfiDmDerOMmx" +
					"/ynSuwqSHp" +
					"+jEVhfP0dlSe72IRV3XKcCKj3nFLpc2Xe2o2ZZSk7Tk99fIlINDA2ymwHrUlMYZdjk6ohHqb6qLVfTSPgJMbAwbPeNDb7bRTWeDnz60g2ul6nrD7NBLiwh3egMBL1ezr8hv0RCJxtGOrEhy6aL7Oy+OQbHpGfVk5f/VRJonqmaz8Y3tNSOQ4GtgDp9enojny845Rv9oVAyfhDAgMBAAECggEAFRIYFKl+xm3l/SGLKDWJ2TwJ0D9krsgceRcAJI7lg+37iExs9TDTbHP+rSGY2AmucnBa/pBFenTRVksIeqzOdelLvpI11XF9z30HpcXL3ENgCW5Nr46hstYJdvhJofxNJBKoU9udorhUK2x1E/apGWkh8x8PHr5U/qUyjgQhfQEKw/+DL/D/5DZnGSAPqG5awp1ebZql7ORYHMMwkdWEYZtgdoiMrE4AZxPIsO9s0OKetvbKBxU0ddmhX41OkpD75Cws5z48Avc4cQPusPQerLgDyCt747Lk+MfRNuZFUPkGOoDa4Mhc5ZNmUP8rKh8HzaCkeXOldTMyO6/Ks9mbVQKBgQDdnX1PqMHZQ/dz0Wo3oNB4i+YIKq9LHHXVOgCpqzUgURAS0x5vDmH3DQgPIiUF2uoSx6rnacK0GcRRm0hJICLADl2z2K/Csu2rQhl1h9uMWp9Crgj3s9Cpm3Hn8O8fOh43vaJ83GUsP4vBwFbInglNLGlsu/oDUr+/Oq069CJDPQKBgQD5WtmTfyHysPlsRJtmKwIuMNOf9huCkSALuQm6LErecyckCUTvcyVKQazBU/N9tLaRITaN2jXum+HBP15E5ZorSbUJF0skk2VHvbmWLQu1VrCajWl8+0OZuHYcuTjGZgNRyhIeEj793LGx4NiZs4bQpZtl7T9CmmiUTCRfJJrhfwKBgG6Yo+rXKzaEJr/T6JAuXxtppbi5pJV1dJCey6ng2Thxe/YJCg0ePmGOTVFQFu3fHCWXI4EtVI+QtFqNhfuIYA88nCKepsghh16KNC5lSHm41/Xs6GjFggLCoou/v3X0CRSEPMzXKF52sAZXOHgR9k5fNZWgEdsKtsp2O/FJ2Ld5AoGAPOZJWVKlceZzJFQlN/CtGabnKJws39id4P8SNxv2NRoepxh0c57FrrsXfCgKwdrO8+cOsdpnbZ04p7/YQC9rVcMsqcm8mnQ9OiZGGz1pLKK9gyGib9nWCscYYa+DBEGJ8gHOJvYB2HJ8G5t0iyVjebXYnC4zUwrvG7j08R+5i/UCgYA5l8RERycJUgrdi4x+xdqG+Zdq1JmwKw5z9KcN2HDY1B9BJUdOR6B9HDlVe4yXxvZ0xMg+w9JAJBNC6Ze21qz4nPW4OT6fazVK3mdQiH/Myl2i8sgTSpnXZRP8DZPULEm8k57uPHWKeVjK0LGzLHnC28Ip2FFSbl/REt6OxkRmng==";
	public static final String KEY = "RESPONSE_KEY";
	private final JsonMapper jsonMapper;
	private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults()
																			   .messageReaders();
	
	{
		JsonMapper.Builder builder = JsonMapper.builder();
		builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
		builder.addModule(new JavaTimeModule());
		builder.configure(JsonReadFeature.ALLOW_TRAILING_COMMA, true);
		builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jsonMapper = builder.build();
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
		List<String> lb1 = request.getHeaders().get("lb_1");
		if (lb1 != null && !lb1.isEmpty()) return chain.filter(exchange);
		HttpHeaders headers = new HttpHeaders();
		headers.putAll(request.getHeaders());
		if (request.getMethod().equals(HttpMethod.GET)) {
			List<String> strings = request.getHeaders().get(KEY);
			if (strings == null || strings.isEmpty()) return chain.filter(exchange);
			byte[] decrypt = RsaUtil.decrypt(RsaUtil.base64.decode(strings
																		   .stream().findFirst()
																		   .orElse("")
																  )
					, PRIVATE_KEY);
			exchange.getResponse().getHeaders().put(KEY,
					List.of(new String(decrypt)));
		}
		Mono<String> objectMono =
				serverRequest
						.bodyToMono(String.class)
						.flatMap(s -> Mono
											  .just(decrypt(s,
													  exchange.getResponse().getHeaders()))
											  .defaultIfEmpty(""));
		BodyInserter<Mono<String>, ReactiveHttpOutputMessage> monoReactiveHttpOutputMessageBodyInserter = BodyInserters.fromPublisher(objectMono, String.class);
		headers.remove(HttpHeaders.CONTENT_LENGTH);
		CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange,
				headers);
		return monoReactiveHttpOutputMessageBodyInserter
					   .insert(outputMessage,
							   new BodyInserterContext())
					   .then(Mono.defer(() -> chain.filter(exchange
																   .mutate()
																   .request(decorator(exchange,
																		   headers
																		   , outputMessage))
																   .build())));
	}
	
	private String decrypt(String message, HttpHeaders headers) {
		try {
			Map<String, String> readValue =
					jsonMapper.readValue(RsaUtil.decrypt(RsaUtil.base64.decode(message),
									PRIVATE_KEY),
							jsonMapper
									.getTypeFactory()
									.constructMapType(HashMap.class, String.class, String.class));
			headers.put(KEY, List.of(readValue.get("value")));
			return readValue.get("payload");
		} catch (IOException e) {
			return "";
		}
	}
	
	private ServerHttpRequestDecorator decorator(ServerWebExchange exchange, HttpHeaders headers,
												 CachedBodyOutputMessage outputMessage) {
		
		return new ServerHttpRequestDecorator(exchange.getRequest()) {
			@Override
			@Nonnull
			public HttpHeaders getHeaders() {
				var length = headers.getContentLength();
				var header = new HttpHeaders();
				header.putAll(super.getHeaders());
				if (length > 0L) {
					header.setContentLength(length);
				} else {
					header.set("Transfer-Encoding", "chunked");
				}
				return header;
			}
			
			@Override
			@Nonnull
			public Flux<DataBuffer> getBody() {
				return outputMessage.getBody();
			}
		};
	}
	

	@Override
	public int getOrder() {
		return 0;
	}
	
	// 可以让配置文件传入密钥
	@Override
	public GatewayFilter apply(NameValueConfig config) {
		return this;
	}
}
