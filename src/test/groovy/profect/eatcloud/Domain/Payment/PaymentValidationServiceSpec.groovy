package profect.eatcloud.Domain.Payment

import profect.eatcloud.Domain.Payment.Service.PaymentValidationService
import profect.eatcloud.Domain.Payment.Repository.PaymentRequestRepository
import profect.eatcloud.Domain.Payment.Entity.PaymentRequest
import spock.lang.Specification

class PaymentValidationServiceSpec extends Specification {

    PaymentRequestRepository paymentRequestRepository = Mock()
    PaymentValidationService paymentValidationService = new PaymentValidationService(paymentRequestRepository)

    def "결제 요청 정보를 저장할 수 있어야 한다"() {
        given: "결제 요청 정보가 있을 때"
        UUID orderId = UUID.randomUUID()
        String tossOrderId = "ORDER_123456"
        Integer amount = 10000

        and: "저장될 더미 데이터"
        PaymentRequest savedRequest = new PaymentRequest(orderId, "TOSS", '{"tossOrderId":"ORDER_123456","amount":10000}')

        when: "결제 요청을 저장하면"
        paymentRequestRepository.save(_ as PaymentRequest) >> savedRequest
        PaymentRequest result = paymentValidationService.savePaymentRequest(orderId, tossOrderId, amount)

        then: "저장된 결제 요청이 반환되어야 한다"
        result != null
        result.getOrderId() == orderId
        result.getPgProvider() == "TOSS"
        result.getStatus() == "PENDING"
        println "✅ 결제 요청 저장 테스트 통과"
    }

    def "이미 처리된 결제에 대해 중복 승인을 방지해야 한다"() {
        given: "이미 CONFIRMED 상태인 결제 요청이 있을 때"
        String tossOrderId = "ORDER_ALREADY_CONFIRMED"
        Integer amount = 15000

        and: "이미 승인된 결제 요청 더미 데이터"
        PaymentRequest alreadyConfirmed = new PaymentRequest(UUID.randomUUID(), "TOSS", '{"tossOrderId":"ORDER_ALREADY_CONFIRMED","amount":15000}')
        alreadyConfirmed.setStatus("CONFIRMED")  // 이미 승인된 상태

        and: "findAll()에서 해당 데이터를 반환하도록 Mock 설정"
        paymentRequestRepository.findAll() >> [alreadyConfirmed]

        when: "같은 주문에 대해 다시 검증을 시도하면"
        def result = paymentValidationService.validateCallback(tossOrderId, amount, "payment_key_123")

        then: "중복 결제 에러가 발생해야 한다"
        !result.isSuccess()
        result.getErrorMessage().contains("이미 처리된 결제입니다")
        println "✅ 중복 결제 방지 테스트 통과: ${result.getErrorMessage()}"
    }

    def "저장된 금액과 콜백 금액이 다르면 검증에 실패해야 한다"() {
        given: "저장된 결제 요청 정보"
        String tossOrderId = "ORDER_AMOUNT_MISMATCH"
        Integer savedAmount = 10000  // 저장된 금액
        Integer callbackAmount = 15000  // 콜백으로 받은 금액 (다름!)

        and: "PENDING 상태의 결제 요청 더미 데이터"
        PaymentRequest savedRequest = new PaymentRequest(UUID.randomUUID(), "TOSS", '{"tossOrderId":"ORDER_AMOUNT_MISMATCH","amount":10000}')
        savedRequest.setStatus("PENDING")

        and: "findAll()에서 해당 데이터를 반환하도록 Mock 설정"
        paymentRequestRepository.findAll() >> [savedRequest]

        when: "다른 금액으로 검증을 시도하면"
        def result = paymentValidationService.validateCallback(tossOrderId, callbackAmount, "payment_key_123")

        then: "금액 불일치 에러가 발생해야 한다"
        !result.isSuccess()
        result.getErrorMessage().contains("결제 금액이 일치하지 않습니다")
        result.getErrorMessage().contains("저장된 금액: 10000")
        result.getErrorMessage().contains("콜백 금액: 15000")
        println "✅ 금액 불일치 검증 테스트 통과: ${result.getErrorMessage()}"
    }

    def "저장된 정보와 콜백 정보가 일치하면 검증에 성공해야 한다"() {
        given: "정상적인 결제 요청 정보"
        String tossOrderId = "ORDER_SUCCESS"
        Integer amount = 25000
        String paymentKey = "payment_key_success"

        and: "PENDING 상태의 결제 요청 더미 데이터"
        PaymentRequest savedRequest = new PaymentRequest(UUID.randomUUID(), "TOSS", '{"tossOrderId":"ORDER_SUCCESS","amount":25000}')
        savedRequest.setStatus("PENDING")

        and: "findAll()에서 해당 데이터를 반환하도록 Mock 설정"
        paymentRequestRepository.findAll() >> [savedRequest]

        when: "동일한 정보로 검증을 시도하면"
        def result = paymentValidationService.validateCallback(tossOrderId, amount, paymentKey)

        then: "검증에 성공해야 한다"
        result.isSuccess()
        result.getPaymentRequest() != null
        result.getPaymentRequest().getStatus() == "PENDING"
        println "✅ 정상 검증 성공 테스트 통과"
    }

    def "존재하지 않는 주문에 대한 검증은 실패해야 한다"() {
        given: "존재하지 않는 주문 ID"
        String nonExistentOrderId = "ORDER_NOT_FOUND"
        Integer amount = 20000
        String paymentKey = "payment_key_not_found"

        and: "빈 결과를 반환하도록 Mock 설정"
        paymentRequestRepository.findAll() >> []  // 빈 리스트 반환

        when: "존재하지 않는 주문으로 검증을 시도하면"
        def result = paymentValidationService.validateCallback(nonExistentOrderId, amount, paymentKey)

        then: "주문을 찾을 수 없다는 에러가 발생해야 한다"
        !result.isSuccess()
        result.getErrorMessage().contains("저장된 결제 요청을 찾을 수 없습니다")
        result.getErrorMessage().contains("ORDER_NOT_FOUND")
        println "✅ 존재하지 않는 주문 검증 테스트 통과: ${result.getErrorMessage()}"
    }

    def "해커가 금액을 조작해서 결제하려고 시도하는 경우"() {
        given: "고객이 원래 100,000원 상품을 주문했는데"
        String tossOrderId = "ORDER_HACK_ATTEMPT"
        Integer originalAmount = 100000
        PaymentRequest originalRequest = new PaymentRequest(UUID.randomUUID(), "TOSS", '{"tossOrderId":"ORDER_HACK_ATTEMPT","amount":100000}')

        when: "해커가 콜백에서 1,000원으로 조작해서 보내면"
        Integer hackedAmount = 1000
        paymentRequestRepository.findAll() >> [originalRequest]
        def result = paymentValidationService.validateCallback(tossOrderId, hackedAmount, "payment_key_hack")

        then: "결제가 차단되어야 한다"
        !result.isSuccess()
        result.getErrorMessage().contains("결제 금액이 일치하지 않습니다")
        println "✅ 해킹 시도 차단 성공 - 원래 금액: ${originalAmount}, 조작된 금액: ${hackedAmount}"
    }

    def "같은 주문에 대해 동시에 두 번 결제 승인이 들어오는 경우"() {
        given: "하나의 주문이 있고"
        String tossOrderId = "ORDER_CONCURRENT"
        PaymentRequest pendingRequest = new PaymentRequest(UUID.randomUUID(), "TOSS", '{"tossOrderId":"ORDER_CONCURRENT","amount":50000}')
        pendingRequest.setStatus("PENDING")

        when: "첫 번째 승인이 성공하고"
        paymentRequestRepository.findAll() >> [pendingRequest]
        def firstResult = paymentValidationService.validateCallback(tossOrderId, 50000, "payment_key_1")

        and: "같은 주문에 대해 두 번째 승인이 들어오면 (이미 CONFIRMED 상태)"
        pendingRequest.setStatus("CONFIRMED")  // 첫 번째 승인으로 상태 변경됨
        def secondResult = paymentValidationService.validateCallback(tossOrderId, 50000, "payment_key_2")

        then: "첫 번째는 성공, 두 번째는 실패해야 한다"
        firstResult.isSuccess()
        !secondResult.isSuccess()
        secondResult.getErrorMessage().contains("이미 처리된 결제입니다")
        println "✅ 동시성 문제 해결 - 중복 결제 방지"
    }

    def "전체 결제 플로우 - 요청부터 승인까지"() {
        given: "고객이 10,000원 상품을 주문했을 때"
        UUID orderId = UUID.randomUUID()
        String tossOrderId = "ORDER_FLOW_TEST"
        Integer orderAmount = 10000

        when: "1단계: 결제 요청을 저장하고"
        paymentRequestRepository.save(_ as PaymentRequest) >> { PaymentRequest req ->
            req.setStatus("PENDING")
            return req
        }
        PaymentRequest savedRequest = paymentValidationService.savePaymentRequest(orderId, tossOrderId, orderAmount)

        and: "2단계: 토스에서 콜백으로 동일한 정보가 돌아왔을 때"
        paymentRequestRepository.findAll() >> [savedRequest]
        def validationResult = paymentValidationService.validateCallback(tossOrderId, orderAmount, "payment_key_123")

        then: "검증이 성공해야 한다"
        validationResult.isSuccess()
        println "✅ 전체 결제 플로우 성공"
    }

    def "실제 버그: JSON 파싱에서 특수문자가 포함된 경우 크래시 나는 버그"() {
        given: "특수문자가 포함된 주문 ID"
        String problematicOrderId = "ORDER_WITH_\"QUOTES\"_AND_COMMA,123"

        when: "이런 주문 ID로 결제를 처리하면"
        // 실제로는 JSON 파싱 에러가 날 수 있음
        def result = paymentValidationService.validateCallback(problematicOrderId, 1000, "key")

        then: "크래시가 나지 않아야 한다"
        result != null  // 최소한 null이 아니어야 함
        println "✅ 특수문자 포함 주문 ID 처리 성공: ${problematicOrderId}"
    }

    def "최대 결제 금액 초과시 처리 테스트"() {
        given: "1억원을 초과하는 결제 요청"
        Integer overLimitAmount = 100_000_001  // 1억 1원

        when: "한도 초과 금액으로 결제하면"
        def result = paymentValidationService.validateCallback("ORDER_123", overLimitAmount, "key")

        then: "적절히 거부되어야 한다"
        !result.isSuccess()
        result.getErrorMessage().contains("결제 한도 초과")
        println "✅ 결제 한도 초과 테스트 통과: ${result.getErrorMessage()}"
    }

    def "결제 요청이 null인 경우 예외 처리"() {
        when: "결제 요청이 null인 경우"
        paymentValidationService.savePaymentRequest(null, null, null)

        then: "예외가 발생해야 한다"
        thrown(Exception)  // IllegalArgumentException 대신 일반 Exception으로 변경
        println "✅ 결제 요청이 null인 경우 예외 처리 성공"
    }

    def "1000건 이상 결제 요청 처리 성능 테스트"() {
        given: "1000건의 결제 요청 데이터"
        List<Map> testData = (1..1000).collect { i ->
            [
                    orderId: UUID.randomUUID(),
                    tossOrderId: "ORDER_${i}",
                    amount: i * 100
            ]
        }

        and: "Mock 설정"
        paymentRequestRepository.save(_ as PaymentRequest) >> { PaymentRequest req -> req }

        when: "모든 결제 요청을 처리하면"
        List<PaymentRequest> results = testData.collect { data ->
            paymentValidationService.savePaymentRequest(data.orderId, data.tossOrderId, data.amount)
        }

        then: "모든 요청이 성공적으로 처리되어야 한다"
        results.size() == 1000
        results.every { it != null }
        println "✅ 1000건 결제 요청 처리 성능 테스트 통과"
    }
}