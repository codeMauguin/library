package Message;


import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Builder
public class RestResponse implements Serializable {
	@Serial
	private static final long serialVersionUID = 610869459789339011L;
	private Integer code;
	
	private String error;
	
	private Object data;
	
	public static RestResponse response(ResponseCode code) {
		return RestResponse.builder()
					   .code(code.code())
					   .error(code.message())
					   .build();
	}
	
	public static RestResponse response(Object data, ResponseCode response) {
		return RestResponse.builder().code(response.code())
					   .error(response.message())
					   .data(data)
					   .build();
	}
	
}
