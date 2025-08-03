package profect.eatcloud.Domain.Admin.exception;

public enum AdminErrorCode {
	ADMIN_NOT_FOUND("ADMIN_001", "해당 관리자를 찾을 수 없습니다"),
	EMAIL_ALREADY_EXISTS("ADMIN_002", "이미 사용 중인 이메일입니다"),
	INVALID_INPUT("ADMIN_003", "잘못된 입력값입니다"),
	// ... 필요에 따라 추가

	;

	private final String code;
	private final String message;

	AdminErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}

