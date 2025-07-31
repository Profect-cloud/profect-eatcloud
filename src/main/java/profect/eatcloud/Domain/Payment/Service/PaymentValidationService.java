package profect.eatcloud.Domain.Payment.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Payment.Entity.PaymentRequest;
import profect.eatcloud.Domain.Payment.Repository.PaymentRequestRepository;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PaymentValidationService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 상수
    private static final Integer MAX_AMOUNT = 100_000_000;  // 1억원

    /**
     * 결제 요청 저장
     */
    public PaymentRequest savePaymentRequest(UUID orderId, String tossOrderId, Integer amount) {
        // 필수 정보 검증
        if (orderId == null) {
            throw new IllegalArgumentException("주문 ID는 필수입니다");
        }
        if (tossOrderId == null || tossOrderId.trim().isEmpty()) {
            throw new IllegalArgumentException("토스 주문 ID는 필수입니다");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다");
        }
        String requestPayload = String.format("{\"tossOrderId\":\"%s\",\"amount\":%d}", tossOrderId, amount);
        PaymentRequest paymentRequest = new PaymentRequest(orderId, "TOSS", requestPayload);
        return paymentRequestRepository.save(paymentRequest);
    }

    /**
     * 콜백 검증
     */
    public ValidationResult validateCallback(String tossOrderId, Integer callbackAmount, String paymentKey) {

        // 1. 기본 검증
        if (tossOrderId == null || callbackAmount == null) {
            return ValidationResult.fail("필수 정보가 누락되었습니다");
        }

        // 2. 한도 검증
        if (callbackAmount > MAX_AMOUNT) {
            return ValidationResult.fail("결제 한도 초과: 최대 " + MAX_AMOUNT + "원");
        }

        // 3. 저장된 요청 찾기
        Optional<PaymentRequest> savedRequest = findByTossOrderId(tossOrderId);
        if (savedRequest.isEmpty()) {
            return ValidationResult.fail("저장된 결제 요청을 찾을 수 없습니다: " + tossOrderId);  // 주문 ID 포함
        }

        PaymentRequest request = savedRequest.get();

        // 4. 중복 체크
        if (!"PENDING".equals(request.getStatus())) {
            return ValidationResult.fail("이미 처리된 결제입니다. 상태: " + request.getStatus());
        }

        // 5. 금액 검증 - 저장된 주문의 실제 결제 금액과 비교
        try {
            Integer savedAmount = extractAmount(request.getRequestPayload());
            if (!savedAmount.equals(callbackAmount)) {
                return ValidationResult.fail(String.format("결제 금액이 일치하지 않습니다. 저장된 금액: %d, 콜백 금액: %d",
                        savedAmount, callbackAmount));
            }
        } catch (Exception e) {
            return ValidationResult.fail("결제 정보 파싱 오류: " + e.getMessage());
        }

        return ValidationResult.success(request);
    }

    // 헬퍼 메서드들
    public Optional<PaymentRequest> findByTossOrderId(String tossOrderId) {
        try {
            List<PaymentRequest> requests = paymentRequestRepository.findAll();

            // null 체크 추가
            if (requests == null) {
                return Optional.empty();
            }

            return requests.stream()
                    .filter(req -> {
                        try {
                            return req.getRequestPayload() != null &&
                                    req.getRequestPayload().contains("\"tossOrderId\":\"" + tossOrderId + "\"");
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .findFirst();
        } catch (Exception e) {
            // 예외 발생시 빈 Optional 반환
            return Optional.empty();
        }
    }

    private Integer extractAmount(String payload) throws Exception {
        if (payload == null || payload.trim().isEmpty()) {
            throw new Exception("결제 정보가 비어있습니다");
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            if (!jsonNode.has("amount")) {
                throw new Exception("금액 정보가 없습니다");
            }
            return jsonNode.get("amount").asInt();
        } catch (Exception e) {
            throw new Exception("JSON 파싱 실패: " + e.getMessage());
        }
    }

    public void updatePaymentStatus(UUID paymentRequestId, String status) {
        paymentRequestRepository.findById(paymentRequestId)
                .ifPresent(request -> {
                    request.setStatus(status);
                    paymentRequestRepository.save(request);
                });
    }

    // ValidationResult 클래스는 동일
    @Getter
    public static class ValidationResult {
        private final boolean success;
        private final String errorMessage;
        private final PaymentRequest paymentRequest;

        private ValidationResult(boolean success, String errorMessage, PaymentRequest paymentRequest) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.paymentRequest = paymentRequest;
        }

        public static ValidationResult success(PaymentRequest paymentRequest) {
            return new ValidationResult(true, null, paymentRequest);
        }

        public static ValidationResult fail(String errorMessage) {
            return new ValidationResult(false, errorMessage, null);
        }

    }
}