package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

public interface test {
	
	@GetMapping("/test")
	default RestResponse test() {
		return RestResponse.response(Map.of("id","12"),ResponseCode.SUCCESS);
	}
}
