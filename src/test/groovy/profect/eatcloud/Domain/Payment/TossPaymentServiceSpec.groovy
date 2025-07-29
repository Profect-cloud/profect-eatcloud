package profect.eatcloud.Domain.Payment

import spock.lang.Specification

class TossPaymentServiceSpec extends Specification {
    
    def "서비스가 정상적으로 생성되는지 테스트"() {
        given: "TossPaymentService가 있을 때"
        TossPaymentService service = new TossPaymentService()
        
        expect: "서비스가 null이 아니어야 한다"
        service != null
    }
}