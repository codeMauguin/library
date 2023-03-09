package Message;


public enum ResponseCode {
	
	SUCCESS(1000, "服务正常"),
	REGISTRY(2002, "注册失败"),
	REGISTRY_NAME(2003, "用户名重复"),
	NO_PERMISSION(2005, "没有权限"),
	ERROR_ROLE(10001, "角色错误，存在危险"),
	
	FAIL_CREATE_ORDER(4000, "重复订单"),
	
	FAIL_COMMIT_ORDER(4001, "订单错误"),
	PARAM_ERROR(3000, "参数错误"),
	FAIL_COMMIT_COMMENT(4000, "评论失败"),
	LOGIN_FAIL(2001, "用户名或者密码错误!!!"),
	CODE_ERROR(20004, "验证码不正确"),
	FAIL_RENEWAL(5000, "续签次数不足"), TIMEOUT(7000, "任务超时");
	private final Integer code;
	private String message;
	
	ResponseCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public ResponseCode setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Integer code() {
		return code;
	}
	
	public String message() {
		return message;
	}
}
