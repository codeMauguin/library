package app.config;


import Message.ResponseCode;
import Message.RestResponse;
import app.exception.NoPermission;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseErrorResponse {
	
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IllegalStateException.class)
	public RestResponse error(IllegalStateException exception) {
		return RestResponse.builder().code(5000).error(exception.getMessage()).build();
	}
	
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(NoPermission.class)
	public RestResponse error() {
		return RestResponse.response(ResponseCode.NO_PERMISSION);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ConstraintViolationException.class)
	public RestResponse errorC() {
		return RestResponse.response(ResponseCode.PARAM_ERROR);
	}
}
