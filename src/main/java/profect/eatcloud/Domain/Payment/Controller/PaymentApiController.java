package profect.eatcloud.Domain.Payment.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Payment.Service.TossPaymentService;
import profect.eatcloud.Domain.Payment.Dto.TossPaymentResponse;
import profect.eatcloud.Domain.Payment.Exception.PaymentValidationException;

import java.util.Map;
import java.util.HashMap;

/**
 * Payment 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentApiController {
    
    @Autowired
    private TossPaymentService tossPaymentService;
    
    /**
     * 결제 승인 API
     * POST /api/payments/confirm
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> request) {
        try {
            String paymentKey = (String) request.get("paymentKey");
            String orderId = (String) request.get("orderId");
            Integer amount = (Integer) request.get("amount");
            
            TossPaymentResponse response = tossPaymentService.confirmPayment(paymentKey, orderId, amount);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("message", "결제가 성공적으로 처리되었습니다.");
            
            return ResponseEntity.ok(result);
            
        } catch (PaymentValidationException e) {
            // GlobalExceptionHandler가 처리하므로 여기서는 그냥 던짐
            throw e;
        } catch (Exception e) {
            // GlobalExceptionHandler가 처리하므로 여기서는 그냥 던짐
            throw e;
        }
    }
    
    /**
     * 결제 상태 확인 API
     * GET /api/payments/status/{orderId}
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String orderId) {
        // TODO: DB에서 주문 상태 조회 로직 구현
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("status", "pending");
        result.put("message", "결제 대기 중입니다.");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 결제 검증 API
     * POST /api/payments/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validatePayment(@RequestBody Map<String, Object> request) {
        try {
            String paymentKey = (String) request.get("paymentKey");
            String orderId = (String) request.get("orderId");
            Integer amount = (Integer) request.get("amount");
            
            // 검증 로직 실행 (TossPaymentService의 validatePaymentRequest 메서드 호출)
            tossPaymentService.confirmPayment(paymentKey, orderId, amount);
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("message", "결제 정보가 유효합니다.");
            
            return ResponseEntity.ok(result);
            
        } catch (PaymentValidationException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("valid", false);
            result.put("error", e.getMessage());
            result.put("errorCode", e.getErrorCode());
            
            return ResponseEntity.badRequest().body(result);
        }
    }
} 