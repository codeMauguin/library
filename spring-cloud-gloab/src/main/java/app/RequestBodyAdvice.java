package app;

import app.annotation.JsonResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nonnull;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.ValidationAnnotationUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RequestBodyAdvice implements HandlerMethodArgumentResolver {
	private final static String REQUEST_JSON_KEY = "JSON_BODY_KEY";
	
	
	private final JsonMapper objectMapper;
	private final Map<MethodParameter, Map.Entry<String, JsonResponse>> CACHE =
			new ConcurrentHashMap<>(255);
	
	
	{
		JsonMapper.Builder builder = JsonMapper.builder();
		builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
		builder.addModule(new JavaTimeModule());
		builder.configure(JsonReadFeature.ALLOW_TRAILING_COMMA, true);
		builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper = builder.build();
	}
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasMethodAnnotation(JsonResponse.class) ||
					   parameter.hasParameterAnnotation(JsonResponse.class);
	}
	
	@Override
	public Object resolveArgument(@Nonnull MethodParameter parameter,
								  ModelAndViewContainer mavContainer,
								  @Nonnull NativeWebRequest webRequest,
								  WebDataBinderFactory binderFactory) throws Exception {
		
		
		Map.Entry<String, JsonResponse> responseEntry = CACHE.get(parameter);
		parameter = parameter.nestedIfOptional();
		if (responseEntry == null) {
			responseEntry = cache(parameter);
		}
		JsonResponse parameterAnnotation = responseEntry.getValue();
		Type nestedGenericParameterType = parameter.getNestedGenericParameterType();
		String parameterName = responseEntry.getKey();
		JsonNode attribute = get(webRequest,
				parameterAnnotation != null && parameterAnnotation.once());
		JsonNode jsonNode = attribute.get(parameterName);
		Object arg = jsonNode == null ?
							 (parameterAnnotation != null && StringUtils.hasText(parameterAnnotation.defaultValue()))
									 ?
									 objectMapper.readValue("\"" + parameterAnnotation.defaultValue() + "\"", objectMapper
																													  .getTypeFactory()
																													  .constructType(nestedGenericParameterType))
									 : null
							 
							 : objectMapper.readValue(jsonNode.traverse(),
				objectMapper.getTypeFactory()
							.constructType(nestedGenericParameterType));
		// 绑定参数验证
		if (binderFactory != null) {
			WebDataBinder binder = binderFactory.createBinder(webRequest,
					arg,
					parameterName);
			validateIfApplicable(binder,
					parameter);
			if (binder.getBindingResult()
					  .hasErrors()) {
				throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
			}
			if (mavContainer != null) {
				mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + parameterName,
						binder.getBindingResult());
			}
		}
		return arg;
	}
	
	/**
	 * > 如果参数有@JsonResponse注解，则返回一个以参数名为key，注解为value的Map.Entry
	 * 并缓存当前参数
	 * @param parameter 要解析的参数。该参数提供对各种方法相关信息的访问，例如方法名称、方法返回类型、方法注释等。
	 * @return 一个 Map.Entry<String, JsonResponse>
	 */
	private Map.Entry<String, JsonResponse> cache(MethodParameter parameter) {
		JsonResponse parameterAnnotation =
				Optional.ofNullable(parameter.getParameterAnnotation(JsonResponse.class))
						.orElse(parameter.getMethodAnnotation(JsonResponse.class));
		assert parameterAnnotation != null;
		String ann = parameterAnnotation.value().length == 1 ?
							 parameterAnnotation.value()[0] :
							 parameterAnnotation.value()[parameter.getParameterIndex()];
		String parameterName = parameterAnnotation.value().length > 0 && StringUtils.hasText(
				ann
																							) ?
									   ann : Optional
													 .ofNullable(parameter.getParameterName())
													 .orElse(Conventions.getVariableNameForParameter(parameter));
		Map.Entry<String, JsonResponse> responseEntry =
				new AbstractMap.SimpleImmutableEntry<>(parameterName,
						parameterAnnotation);
		CACHE.put(parameter, responseEntry);
		return responseEntry;
	}
	
	public JsonNode get(@Nonnull NativeWebRequest webRequest,
						boolean once) throws IOException {
		JsonNode attribute = (JsonNode) webRequest.getAttribute(REQUEST_JSON_KEY,
				RequestAttributes.SCOPE_REQUEST);
		if (attribute == null) {
			HttpServletRequest nativeRequest =
					webRequest.getNativeRequest(HttpServletRequest.class);
			Assert.notNull(nativeRequest,
					"请求的http servlet 为空");
			byte[] body;
			try (ServletInputStream inputStream = nativeRequest.getInputStream()) {
				body = new byte[1024];
				int index = 0;
				int i;
				byte[] buffer = new byte[1024];
				while ((i = inputStream.read(buffer)) != -1) {
					if ((index + i) >= body.length) {
						body = Arrays.copyOf(body,
								body.length << 1);
					}
					System.arraycopy(buffer,
							0,
							body,
							index,
							i);
					index += i;
				}
				body = Arrays.copyOf(body,
						index);
			}
			JsonNode jsonNode = objectMapper.readTree(body);
			if (!once) webRequest.setAttribute(REQUEST_JSON_KEY,
					jsonNode,
					RequestAttributes.SCOPE_REQUEST);
			return jsonNode;
		} else return attribute;
	}
	
	protected void validateIfApplicable(WebDataBinder binder,
										MethodParameter parameter) {
		Annotation[] annotations = parameter.getParameterAnnotations();
		for (Annotation ann : annotations) {
			Object[] validationHints = ValidationAnnotationUtils.determineValidationHints(ann);
			if (validationHints != null) {
				binder.validate(validationHints);
				break;
			}
		}
	}
}
