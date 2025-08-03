package profect.eatcloud.Domain.Admin.message;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseMessage {
	STORE_DELETE_SUCCESS("가게 삭제 완료"),
	CUSTOMER_BAN_SUCCESS("고객 밴 처리 완료"),
	MANAGER_BAN_SUCCESS("매니저 밴 처리 완료"),
	APPLICATION_APPROVE_SUCCESS("신청서 승인 완료"),
	APPLICATION_REJECT_SUCCESS("신청서 거절 완료");

	private final String message;

	ResponseMessage(String message) {
		this.message = message;
	}

	@JsonValue
	public String getMessage() {
		return this.message;
	}

	public String format(Object... args) {
		return String.format(this.message, args);
	}

}

