package profect.eatcloud.Config

import profect.eatcloud.Domain.Payment.Exception.PaymentException
import profect.eatcloud.Domain.Payment.Exception.PaymentNotFoundException
import profect.eatcloud.Domain.Payment.Exception.PaymentValidationException
import spock.lang.Specification
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class GlobalExceptionHandlerSpec extends Specification {
    
    GlobalExceptionHandler handler
    
    def setup() {
        handler = new GlobalExceptionHandler()
    }
    
    def "PaymentNotFoundException 처리 테스트"() {
        given: "PaymentNotFoundException이 발생했을 때"
        PaymentNotFoundException exception = new PaymentNotFoundException("ORDER_123")
        
        when: "예외를 처리하면"
        ResponseEntity<Map<String, Object>> response = handler.handlePaymentNotFound(exception)
        
        then: "404 상태코드와 적절한 응답이 반환되어야 한다"
        response.getStatusCode() == HttpStatus.NOT_FOUND
        response.getBody().get("error") == "PAYMENT_NOT_FOUND"
        response.getBody().get("message").contains("ORDER_123")
        response.getBody().get("errorCode") == "PAYMENT_NOT_FOUND"
    }
    
    def "PaymentValidationException 처리 테스트"() {
        given: "PaymentValidationException이 발생했을 때"
        PaymentValidationException exception = new PaymentValidationException("amount", "결제 금액은 0보다 커야 합니다.")
        
        when: "예외를 처리하면"
        ResponseEntity<Map<String, Object>> response = handler.handlePaymentValidation(exception)
        
        then: "400 상태코드와 적절한 응답이 반환되어야 한다"
        response.getStatusCode() == HttpStatus.BAD_REQUEST
        response.getBody().get("error") == "PAYMENT_VALIDATION_ERROR"
        response.getBody().get("message").contains("결제 금액은 0보다 커야 합니다.")
        response.getBody().get("errorCode") == "PAYMENT_VALIDATION_ERROR"
    }
    
    def "PaymentException 처리 테스트"() {
        given: "PaymentException이 발생했을 때"
        PaymentException exception = new PaymentException("결제 처리 중 오류가 발생했습니다.", "PAYMENT_CONFIRM_ERROR")
        
        when: "예외를 처리하면"
        ResponseEntity<Map<String, Object>> response = handler.handlePaymentError(exception)
        
        then: "500 상태코드와 적절한 응답이 반환되어야 한다"
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        response.getBody().get("error") == "PAYMENT_ERROR"
        response.getBody().get("message") == "결제 처리 중 오류가 발생했습니다."
        response.getBody().get("errorCode") == "PAYMENT_CONFIRM_ERROR"
    }
} 