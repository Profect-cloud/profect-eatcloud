package profect.eatcloud.Domain.Payment.Exception;

/**
 * Payment를 찾을 수 없을 때 발생하는 예외
 */
public class PaymentNotFoundException extends PaymentException {
    
    public PaymentNotFoundException(String message) {
        super(message, "PAYMENT_NOT_FOUND");
    }
    
    public PaymentNotFoundException(String orderId, boolean isOrderId) {
        super("주문 ID '" + orderId + "'에 해당하는 결제 정보를 찾을 수 없습니다.", "PAYMENT_NOT_FOUND");
    }
} 