package profect.eatcloud.Domain.Payment.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Payment.Service.TossPaymentService;
import profect.eatcloud.Domain.Payment.Service.PaymentValidationService;
import profect.eatcloud.Domain.Payment.Service.PointService;
import profect.eatcloud.Domain.Payment.Dto.TossPaymentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 토스페이먼츠 표준 결제 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@Tag(name = "9. PaymentViewController")
public class PaymentController {

    private final TossPaymentService tossPaymentService;
    private final PaymentValidationService paymentValidationService;
    private final PointService pointService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${toss.client-key}")
    private String clientKey;

    /**
     * 주문 페이지 표시
     */
    @Operation(summary = "주문 페이지", description = "결제 주문 페이지를 표시합니다.")
    @GetMapping("/order")
    public String orderPage(Model model) {
        // 임시 고객 정보 (실제로는 세션에서 가져와야 함)
        String customerId = "customer_123";
        Integer customerPoints = 5000;  // 실제로는 DB에서 조회

        model.addAttribute("customerId", customerId);
        model.addAttribute("customerPoints", customerPoints);

        return "order/order";
    }

    /**
     * 주문 → 결제 페이지 이동
     */
    @Operation(summary = "결제 페이지", description = "주문 정보를 받아 결제 페이지로 이동합니다.")
    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkoutPage(@RequestParam("orderData") String orderDataJson) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // JSON 파싱
            Map<String, Object> orderData = objectMapper.readValue(orderDataJson, Map.class);

            // 주문 정보 추출
            String customerId = (String) orderData.get("customerId");
            Integer totalAmount = (Integer) orderData.get("totalAmount");
            Boolean usePoints = (Boolean) orderData.get("usePoints");
            Integer pointsToUse = (Integer) orderData.get("pointsToUse");
            Integer finalPaymentAmount = (Integer) orderData.get("finalPaymentAmount");

            // 포인트 사용 처리
            if (usePoints && pointsToUse > 0) {
                try {
                    UUID customerUUID = UUID.fromString(customerId);
                    var pointResult = pointService.usePoints(customerUUID, pointsToUse);

                    if (!pointResult.isSuccess()) {
                        response.put("error", pointResult.getErrorMessage());
                        return ResponseEntity.badRequest().body(response);
                    }
                } catch (IllegalArgumentException e) {
                    // UUID 형식이 아닌 경우 임시 처리 (개발/테스트용)
                    response.put("warning", "고객 ID가 UUID 형식이 아닙니다. 포인트 사용을 건너뜁니다.");
                    // 실제 운영에서는 적절한 고객 ID를 사용해야 합니다.
                }
            }

            // 토스 주문 ID 생성
            String tossOrderId = "ORDER_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 결제 요청 정보 저장 (실제 결제 금액만)
            UUID orderUuid = UUID.randomUUID();
            if (finalPaymentAmount > 0) {
                paymentValidationService.savePaymentRequest(orderUuid, tossOrderId, finalPaymentAmount);
            }

            // 결제 페이지에 전달할 데이터
            response.put("orderId", tossOrderId);
            response.put("amount", finalPaymentAmount);
            response.put("userId", customerId);
            response.put("clientKey", clientKey);
            response.put("usePoints", usePoints);
            response.put("pointsUsed", pointsToUse);
            response.put("originalAmount", totalAmount);
            response.put("orderItems", orderData.get("items"));
            response.put("message", "결제 페이지 데이터 생성 성공");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "주문 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 기존 결제 페이지 (포인트 충전용)
     */
    @Operation(summary = "포인트 충전 페이지", description = "포인트 충전을 위한 결제 페이지를 표시합니다.")
    @GetMapping("/charge")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPaymentPage(@RequestParam(required = false) String userId,
                                 @RequestParam(required = false) Integer amount) {

        Map<String, Object> response = new HashMap<>();

        // 기본값 설정
        if (userId == null) userId = "user_" + System.currentTimeMillis();
        if (amount == null) amount = 1;

        // 주문번호 생성
        String orderId = "ORDER_" + UUID.randomUUID().toString().substring(0, 12);

        // 응답 데이터 설정
        response.put("userId", userId);
        response.put("clientKey", clientKey);
        response.put("amount", amount);
        response.put("orderId", orderId);
        response.put("message", "포인트 충전 페이지 데이터 생성 성공");

        return ResponseEntity.ok(response);
    }

    /**
     * 결제 성공 콜백
     */
    @Operation(summary = "결제 성공 콜백", description = "토스페이먼츠 결제 성공 콜백을 처리합니다.")
    @GetMapping("/success")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam Integer amount) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 결제 정보 검증
            var validationResult = paymentValidationService.validateCallback(orderId, amount, paymentKey);

            if (!validationResult.isSuccess()) {
                response.put("error", validationResult.getErrorMessage());
                return ResponseEntity.badRequest().body(response);
            }

            // 토스페이먼츠 승인 API 호출
            TossPaymentResponse tossResponse = tossPaymentService.confirmPayment(paymentKey, orderId, amount);

            // 성공 응답 데이터
            response.put("paymentKey", paymentKey);
            response.put("orderId", orderId);
            response.put("amount", amount);
            response.put("status", tossResponse.getStatus());
            response.put("method", tossResponse.getMethod());
            response.put("approvedAt", tossResponse.getApprovedAt());
            response.put("message", "결제가 성공적으로 처리되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 결제 실패 콜백
     */
    @Operation(summary = "결제 실패 콜백", description = "토스페이먼츠 결제 실패 콜백을 처리합니다.")
    @GetMapping("/fail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> paymentFail(@RequestParam(required = false) String message,
                              @RequestParam(required = false) String code,
                              @RequestParam(required = false) String orderId) {

        Map<String, Object> response = new HashMap<>();

        // 포인트 롤백 처리 (필요시)
        if (orderId != null) {
            try {
                //TODO:  실제로는 주문 ID로 포인트 사용 내역을 찾아서 롤백
                // pointService.refundPoints(customerId, pointsToRefund);
            } catch (Exception e) {
            }
        }

        response.put("message", message != null ? message : "알 수 없는 오류가 발생했습니다.");
        response.put("code", code);
        response.put("orderId", orderId);
        response.put("status", "FAILED");

        return ResponseEntity.badRequest().body(response);
    }
}