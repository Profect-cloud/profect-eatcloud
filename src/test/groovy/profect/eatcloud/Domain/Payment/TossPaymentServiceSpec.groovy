package profect.eatcloud.Domain.Payment

import profect.eatcloud.Domain.Payment.Service.TossPaymentService
import profect.eatcloud.Domain.Payment.Exception.PaymentValidationException
import profect.eatcloud.Domain.Payment.Exception.PaymentException
import spock.lang.Specification

class TossPaymentServiceSpec extends Specification {
    
    TossPaymentService service
    
    def setup() {
        service = new TossPaymentService()
    }
    
    def "서비스가 정상적으로 생성되는지 테스트"() {
        expect: "서비스가 null이 아니어야 한다"
        service != null
    }
    
    def "결제 검증 - paymentKey가 null일 때 예외 발생"() {
        when: "paymentKey가 null인 경우"
        service.confirmPayment(null, "ORDER_123", 1000)
        
        then: "PaymentValidationException이 발생해야 한다"
        thrown(PaymentValidationException)
    }
    
    def "결제 검증 - paymentKey가 빈 문자열일 때 예외 발생"() {
        when: "paymentKey가 빈 문자열인 경우"
        service.confirmPayment("", "ORDER_123", 1000)
        
        then: "PaymentValidationException이 발생해야 한다"
        thrown(PaymentValidationException)
    }
    
    def "결제 검증 - orderId가 null일 때 예외 발생"() {
        when: "orderId가 null인 경우"
        service.confirmPayment("PAYMENT_KEY_123", null, 1000)
        
        then: "PaymentValidationException이 발생해야 한다"
        thrown(PaymentValidationException)
    }
    
    def "결제 검증 - amount가 null일 때 예외 발생"() {
        when: "amount가 null인 경우"
        service.confirmPayment("PAYMENT_KEY_123", "ORDER_123", null)
        
        then: "PaymentValidationException이 발생해야 한다"
        thrown(PaymentValidationException)
    }
    
    def "결제 검증 - amount가 0 이하일 때 예외 발생"() {
        when: "amount가 0 이하인 경우"
        service.confirmPayment("PAYMENT_KEY_123", "ORDER_123", 0)
        
        then: "PaymentValidationException이 발생해야 한다"
        thrown(PaymentValidationException)
    }
}