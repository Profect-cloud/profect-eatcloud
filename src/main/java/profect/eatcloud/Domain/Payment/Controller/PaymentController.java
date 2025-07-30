package profect.eatcloud.Domain.Payment.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Map;
import java.util.UUID;

/**
 * 토스페이먼츠 표준 결제 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@Tag(name = "10. PaymentViewController")
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
    public String checkoutPage(@RequestParam("orderData") String orderDataJson, Model model) {
        System.out.println("=== /api/v1/payment/checkout 호출됨 ==="); // 이 로그가 나오는지 확인
        System.out.println("orderDataJson: " + orderDataJson);
        try {
            // JSON 파싱
            Map<String, Object> orderData = objectMapper.readValue(orderDataJson, Map.class);

            System.out.println("=== 주문 데이터 받음 ===");
            System.out.println("주문 데이터: " + orderData);

            // 주문 정보 추출
            String customerId = (String) orderData.get("customerId");
            Integer totalAmount = (Integer) orderData.get("totalAmount");
            Boolean usePoints = (Boolean) orderData.get("usePoints");
            Integer pointsToUse = (Integer) orderData.get("pointsToUse");
            Integer finalPaymentAmount = (Integer) orderData.get("finalPaymentAmount");

            // 포인트 사용 처리
            if (usePoints && pointsToUse > 0) {
                // 포인트 차감 (실제 처리)
                UUID customerUUID = UUID.fromString(customerId); // customerId를 UUID로 변환
                var pointResult = pointService.usePoints(customerUUID, pointsToUse);

                if (!pointResult.isSuccess()) {
                    model.addAttribute("error", pointResult.getErrorMessage());
                    return "payment/fail";
                }

                System.out.println("포인트 " + pointsToUse + "원 차감 완료");
            }

            // 토스 주문 ID 생성
            String tossOrderId = "ORDER_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 결제 요청 정보 저장 (실제 결제 금액만)
            UUID orderUuid = UUID.randomUUID();
            if (finalPaymentAmount > 0) {
                paymentValidationService.savePaymentRequest(orderUuid, tossOrderId, finalPaymentAmount);
            }

            // 결제 페이지에 전달할 데이터
            model.addAttribute("orderId", tossOrderId);
            model.addAttribute("amount", finalPaymentAmount);
            model.addAttribute("userId", customerId);
            model.addAttribute("clientKey", clientKey);
            model.addAttribute("usePoints", usePoints);
            model.addAttribute("pointsUsed", pointsToUse);
            model.addAttribute("originalAmount", totalAmount);
            model.addAttribute("orderItems", orderData.get("items"));

            return "payment/checkout";

        } catch (Exception e) {
            System.err.println("주문 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/api/v1/payment/order?error=processing";
        }
    }

    /**
     * 기존 결제 페이지 (포인트 충전용)
     */
    @Operation(summary = "포인트 충전 페이지", description = "포인트 충전을 위한 결제 페이지를 표시합니다.")
    @GetMapping("/charge")
    public String getPaymentPage(@RequestParam(required = false) String userId,
                                 @RequestParam(required = false) Integer amount,
                                 Model model) {

        System.out.println("=== 결제 페이지 요청 ===");
        System.out.println("User ID: " + userId);
        System.out.println("Amount: " + amount);

        // 기본값 설정
        if (userId == null) userId = "user_" + System.currentTimeMillis();
        if (amount == null) amount = 1;

        // 주문번호 생성
        String orderId = "ORDER_" + UUID.randomUUID().toString().substring(0, 12);

        // 템플릿에 전달할 데이터 설정
        model.addAttribute("userId", userId);
        model.addAttribute("clientKey", clientKey);
        model.addAttribute("amount", amount);
        model.addAttribute("orderId", orderId);

        return "payment/checkout";
    }

    /**
     * 결제 성공 콜백
     */
    @Operation(summary = "결제 성공 콜백", description = "토스페이먼츠 결제 성공 콜백을 처리합니다.")
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam Integer amount,
                                 Model model) {
        System.out.println("=== 결제 성공 콜백 수신 ===");
        System.out.println("Payment Key: " + paymentKey);
        System.out.println("Order ID: " + orderId);
        System.out.println("Amount: " + amount);

        try {
            // 결제 정보 검증
            var validationResult = paymentValidationService.validateCallback(orderId, amount, paymentKey);

            if (!validationResult.isSuccess()) {
                System.err.println("결제 검증 실패: " + validationResult.getErrorMessage());
                model.addAttribute("error", validationResult.getErrorMessage());
                return "payment/fail";
            }

            // 토스페이먼츠 승인 API 호출
            TossPaymentResponse response = tossPaymentService.confirmPayment(paymentKey, orderId, amount);

            // 성공 페이지에 전달할 데이터
            model.addAttribute("paymentKey", paymentKey);
            model.addAttribute("orderId", orderId);
            model.addAttribute("amount", amount);
            model.addAttribute("status", response.getStatus());
            model.addAttribute("method", response.getMethod());
            model.addAttribute("approvedAt", response.getApprovedAt());

            System.out.println("=== 결제 처리 완료 ===");

            return "payment/success";

        } catch (Exception e) {
            System.err.println("결제 처리 중 오류: " + e.getMessage());
            model.addAttribute("error", "결제 처리 중 오류가 발생했습니다.");
            return "payment/fail";
        }
    }

    /**
     * 결제 실패 콜백
     */
    @Operation(summary = "결제 실패 콜백", description = "토스페이먼츠 결제 실패 콜백을 처리합니다.")
    @GetMapping("/fail")
    public String paymentFail(@RequestParam(required = false) String message,
                              @RequestParam(required = false) String code,
                              @RequestParam(required = false) String orderId,
                              Model model) {
        System.out.println("=== 결제 실패 콜백 수신 ===");
        System.out.println("Error Message: " + message);
        System.out.println("Error Code: " + code);
        System.out.println("Order ID: " + orderId);

        // 포인트 롤백 처리 (필요시)
        if (orderId != null) {
            try {
                // 실제로는 주문 ID로 포인트 사용 내역을 찾아서 롤백
                // 여기서는 예시로 임시 구현
                System.out.println("포인트 롤백 처리 필요: " + orderId);
                // pointService.refundPoints(customerId, pointsToRefund);
            } catch (Exception e) {
                System.err.println("포인트 롤백 중 오류: " + e.getMessage());
            }
        }

        model.addAttribute("message", message != null ? message : "알 수 없는 오류가 발생했습니다.");
        model.addAttribute("code", code);
        model.addAttribute("orderId", orderId);

        return "payment/fail";
    }
}