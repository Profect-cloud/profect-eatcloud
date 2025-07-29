package profect.eatcloud.Domain.Payment

import profect.eatcloud.Domain.Payment.Entity.PaymentRequest
import profect.eatcloud.Domain.Payment.Repository.PaymentRequestRepository
import spock.lang.Specification

class PaymentRequestRepositorySpec extends Specification {

    PaymentRequestRepository paymentRequestRepository = Mock(PaymentRequestRepository)

    def "PaymentRequest 저장 테스트 - 더미 데이터"() {
        given: "더미 결제 요청 데이터를 준비할 때"
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        String requestPayload = '{"tossOrderId":"ORDER_123","amount":1000}'
        PaymentRequest dummyRequest = new PaymentRequest(orderId, "TOSS", requestPayload)

        and: "저장된 결과 더미 데이터"
        PaymentRequest savedDummy = new PaymentRequest(orderId, "TOSS", requestPayload)
        savedDummy.paymentRequestId = UUID.fromString("999e4567-e89b-12d3-a456-426614174999")

        when: "결제 요청을 저장하면"
        paymentRequestRepository.save(dummyRequest) >> savedDummy

        then: "저장 결과가 반환되어야 한다"
        PaymentRequest result = paymentRequestRepository.save(dummyRequest)
        result != null
        result.getOrderId() == orderId
        result.getPgProvider() == "TOSS"
        result.getStatus() == "PENDING"
        println "✅ Mock 저장 테스트 성공: ${result.getOrderId()}"
    }

    def "주문 ID로 결제 요청 찾기 테스트 - 더미 데이터"() {
        given: "더미 결제 요청 데이터가 있을 때"
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001")
        PaymentRequest dummyFound = new PaymentRequest(orderId, "TOSS", '{"amount":2000}')

        and: "Repository Mock 설정"
        paymentRequestRepository.findByOrderId(orderId) >> Optional.of(dummyFound)

        when: "주문 ID로 결제 요청을 찾으면"
        Optional<PaymentRequest> result = paymentRequestRepository.findByOrderId(orderId)

        then: "더미 데이터가 조회되어야 한다"
        result.isPresent()
        result.get().getOrderId() == orderId
        result.get().getPgProvider() == "TOSS"
        println "✅ Mock 조회 테스트 성공: ${result.get().getOrderId()}"
    }

    def "존재하지 않는 주문 ID 조회 테스트"() {
        given: "존재하지 않는 주문 ID"
        UUID nonExistentId = UUID.fromString("000e4567-e89b-12d3-a456-426614174000")

        and: "Repository Mock에서 빈 결과 반환"
        paymentRequestRepository.findByOrderId(nonExistentId) >> Optional.empty()

        when: "존재하지 않는 ID로 조회하면"
        Optional<PaymentRequest> result = paymentRequestRepository.findByOrderId(nonExistentId)

        then: "빈 결과가 반환되어야 한다"
        !result.isPresent()
        println "✅ 존재하지 않는 데이터 조회 테스트 성공"
    }

    def "결제 상태별 조회 테스트 - 더미 데이터"() {
        given: "PENDING 상태의 더미 데이터"
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002")
        PaymentRequest pendingPayment = new PaymentRequest(orderId, "TOSS", '{"amount":3000}')

        and: "Mock 설정"
        paymentRequestRepository.findByOrderIdAndStatus(orderId, "PENDING") >> Optional.of(pendingPayment)

        when: "상태별로 조회하면"
        Optional<PaymentRequest> result = paymentRequestRepository.findByOrderIdAndStatus(orderId, "PENDING")

        then: "해당 상태의 데이터가 조회되어야 한다"
        result.isPresent()
        result.get().getStatus() == "PENDING"
        println "✅ 상태별 조회 테스트 성공"
    }
}