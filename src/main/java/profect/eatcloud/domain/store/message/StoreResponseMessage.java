package profect.eatcloud.domain.store.message;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StoreResponseMessage {
    STORE_UPDATE_SUCCESS("가게 정보 수정 완료"),
    STORE_REGISTRATION_REQUEST_SUCCESS("가게 등록 요청 완료"),
    STORE_CLOSURE_REQUEST_SUCCESS("가게 폐업 요청 완료");

    private final String message;

    StoreResponseMessage(String message) {
        this.message = message;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(this.message, args);
    }
}
