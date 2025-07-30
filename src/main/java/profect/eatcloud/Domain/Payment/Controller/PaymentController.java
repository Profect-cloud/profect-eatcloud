package profect.eatcloud.Domain.Payment.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
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
import java.util.Optional;

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
    private final CustomerRepository customerRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${toss.client-key}")
    private String clientKey;

    /**
     * 현재 인증된 고객 정보 조회
     */
    private Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        String customerIdStr = authentication.getName();
        try {
            UUID customerId = UUID.fromString(customerIdStr);
            return customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다."));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 고객 ID 형식입니다.");
        }
    }

    /**
     * 주문 페이지 표시
     */
    @Operation(summary = "주문 페이지", description = "결제 주문 페이지를 표시합니다.")
    @GetMapping("/order")
    public String orderPage(Model model) {
        try {
            Customer customer = getCurrentCustomer();
            model.addAttribute("customerId", customer.getId().toString());
            model.addAttribute("customerPoints", customer.getPoints() != null ? customer.getPoints() : 0);
            model.addAttribute("customerName", customer.getName());
        } catch (Exception e) {
            // 인증 실패시 기본값 설정 (개발/테스트용)
            model.addAttribute("customerId", "test-customer");
            model.addAttribute("customerPoints", 0);
            model.addAttribute("customerName", "테스트 고객");
            model.addAttribute("authError", "인증 정보를 가져올 수 없습니다: " + e.getMessage());
        }

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
            String customerIdFromForm = (String) orderData.get("customerId");
            Integer totalAmount = (Integer) orderData.get("totalAmount");
            Boolean usePoints = (Boolean) orderData.get("usePoints");
            Integer pointsToUse = (Integer) orderData.get("pointsToUse");
            Integer finalPaymentAmount = (Integer) orderData.get("finalPaymentAmount");

            // 인증된 고객 정보 확인
            Customer customer = null;
            try {
                customer = getCurrentCustomer();
                
                // 폼에서 온 고객 ID와 인증된 고객 ID 일치 확인
                if (!customer.getId().toString().equals(customerIdFromForm) && 
                    !"test-customer".equals(customerIdFromForm)) {
                    response.put("error", "인증된 고객 정보와 주문 정보가 일치하지 않습니다.");
                    return ResponseEntity.badRequest().body(response);
                }
                
            } catch (Exception e) {
                // 인증 실패시 처리 (개발/테스트용)
                if (!"test-customer".equals(customerIdFromForm)) {
                    response.put("error", "고객 인증이 필요합니다: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // 포인트 사용 처리
            if (usePoints && pointsToUse > 0 && customer != null) {
                var pointResult = pointService.usePoints(customer.getId(), pointsToUse);

                if (!pointResult.isSuccess()) {
                    response.put("error", pointResult.getErrorMessage());
                    return ResponseEntity.badRequest().body(response);
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
            response.put("userId", customer != null ? customer.getId().toString() : customerIdFromForm);
            response.put("clientKey", clientKey);
            response.put("usePoints", usePoints);
            response.put("pointsUsed", pointsToUse);
            response.put("originalAmount", totalAmount);
            response.put("orderItems", orderData.get("items"));
            response.put("customerName", customer != null ? customer.getName() : "테스트 고객");
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

        // 인증된 고객 정보 사용
        try {
            Customer customer = getCurrentCustomer();
            userId = customer.getId().toString();
        } catch (Exception e) {
            // 인증 실패시 기본값 사용
            if (userId == null) userId = "user_" + System.currentTimeMillis();
        }

        // 기본값 설정
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
                // 실제로는 주문 ID로 포인트 사용 내역을 찾아서 롤백
                // TODO: 주문과 포인트 사용 내역을 연결하여 롤백 처리
                // Customer customer = getCurrentCustomer();
                // pointService.refundPoints(customer.getId(), pointsToRefund);
            } catch (Exception e) {
                // 롤백 실패시 로그 기록
                System.err.println("포인트 롤백 실패: " + e.getMessage());
            }
        }

        response.put("message", message != null ? message : "알 수 없는 오류가 발생했습니다.");
        response.put("code", code);
        response.put("orderId", orderId);
        response.put("status", "FAILED");

        return ResponseEntity.badRequest().body(response);
    }
}