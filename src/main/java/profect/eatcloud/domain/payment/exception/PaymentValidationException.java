package profect.eatcloud.domain.payment.exception;

/**
 * Payment 검증 실패 시 발생하는 예외
 */
public class PaymentValidationException extends PaymentException {
    public PaymentValidationException(String field, String reason) {
        super("결제 검증 실패: " + field + " - " + reason, "PAYMENT_VALIDATION_ERROR");
    }
} 