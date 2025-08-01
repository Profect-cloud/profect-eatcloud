package profect.eatcloud.Domain.Payment.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Payment.Entity.PaymentRequest;
import profect.eatcloud.Domain.Payment.Entity.Payment;
import profect.eatcloud.Domain.Payment.Service.TossPaymentService;
import profect.eatcloud.Domain.Payment.Service.PaymentValidationService;
import profect.eatcloud.Domain.Payment.Service.PaymentAuthenticationService;
import profect.eatcloud.Domain.Payment.Service.PointService;
import profect.eatcloud.Domain.Payment.Service.PaymentService;
import profect.eatcloud.Domain.Payment.Dto.TossPaymentResponse;
import profect.eatcloud.Domain.Order.Service.OrderService;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Domain.Order.Dto.OrderMenu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

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
    private final PaymentAuthenticationService paymentAuthenticationService;
    private final PointService pointService;
    private final CustomerRepository customerRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${toss.client-key}")
    private String clientKey;

    /**
     * 주문 페이지 표시
     */
    @Operation(summary = "주문 페이지", description = "결제 주문 페이지를 표시합니다.")
    @GetMapping("/order")
    public String orderPage(Model model) {
        var authResult = paymentAuthenticationService.validateCustomerForOrderPage();
        
        if (!authResult.isSuccess()) {
            return "redirect:/error/unauthorized?error=" + java.net.URLEncoder.encode(authResult.getErrorMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
        
        model.addAttribute("customerId", authResult.getCustomerIdAsString());
        model.addAttribute("customerPoints", authResult.getCustomerPoints());
        model.addAttribute("customerName", authResult.getCustomerName());
        
        return "order/order";
    }

    @Operation(summary = "결제 페이지", description = "주문 정보를 받아 결제 페이지로 이동합니다.")
    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkoutPage(@RequestParam("orderData") String orderDataJson) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> orderData = objectMapper.readValue(orderDataJson, Map.class);

            String customerIdFromForm = (String) orderData.get("customerId");
            Integer totalAmount = (Integer) orderData.get("totalPrice");
            Boolean usePoints = (Boolean) orderData.getOrDefault("usePoints", false);
            Integer pointsToUse = (Integer) orderData.getOrDefault("pointsToUse", 0);
            Integer finalPaymentAmount = (Integer) orderData.getOrDefault("finalPaymentAmount", totalAmount);
            String orderType = (String) orderData.getOrDefault("orderType", "DELIVERY");
            
            UUID storeId;
            if (orderData.get("storeId") != null) {
                storeId = UUID.fromString((String) orderData.get("storeId"));
            }
            else {
                storeId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }

            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) orderData.get("orderMenuList");
            List<OrderMenu> orderMenuList = new ArrayList<>();
            
            if (itemsData != null) {
                for (Map<String, Object> item : itemsData) {
                    OrderMenu orderMenu = OrderMenu.builder()
                            .menuId(UUID.fromString((String) item.get("menuId")))
                            .menuName((String) item.get("menuName"))
                            .price((Integer) item.get("price"))
                            .quantity((Integer) item.get("quantity"))
                            .build();
                    orderMenuList.add(orderMenu);
                }
            }

            // 고객 인증 및 검증
            var authResult = paymentAuthenticationService.validateCustomerForPayment(customerIdFromForm);
            
            if (!authResult.isSuccess()) {
                response.put("error", authResult.getErrorMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            UUID customerUuid = authResult.getCustomerId();

            Order createdOrder = orderService.createPendingOrder(customerUuid, storeId, orderMenuList, orderType, 
                                                                usePoints, pointsToUse);
            
            if (usePoints != null && usePoints && pointsToUse != null && pointsToUse > 0 && authResult.getCustomer() != null) {
                var pointResult = pointService.usePoints(authResult.getCustomerId(), pointsToUse);

                if (!pointResult.isSuccess()) {
                    orderService.cancelOrder(createdOrder.getOrderId());
                    
                    // 포인트 부족인 경우 상세 정보와 함께 에러 응답
                    if (pointResult.getErrorMessage().contains("부족")) {
                        response.put("error", pointResult.getErrorMessage());
                        response.put("errorType", "INSUFFICIENT_POINTS");
                        response.put("currentPoints", authResult.getCustomerPoints());
                        response.put("requestedPoints", pointsToUse);
                        response.put("redirectUrl", "/error/insufficient-points?currentPoints=" + 
                                   authResult.getCustomerPoints() + "&requestedPoints=" + pointsToUse);
                    } else {
                        response.put("error", pointResult.getErrorMessage());
                        response.put("errorType", "POINT_ERROR");
                    }
                    
                    return ResponseEntity.badRequest().body(response);
                }
            }

            String tossOrderId = "TOSS_" + createdOrder.getOrderId().toString().replace("-", "").substring(0, 16).toUpperCase();

            if (finalPaymentAmount > 0) {
                paymentValidationService.savePaymentRequest(createdOrder.getOrderId(), tossOrderId, finalPaymentAmount);
            }

            response.put("orderId", tossOrderId);
            response.put("internalOrderId", createdOrder.getOrderId().toString());
            response.put("orderNumber", createdOrder.getOrderNumber());
            response.put("amount", finalPaymentAmount);
            response.put("userId", customerUuid.toString());
            response.put("clientKey", clientKey);
            response.put("usePoints", usePoints);
            response.put("pointsUsed", pointsToUse);
            response.put("originalAmount", totalAmount);
            response.put("orderItems", orderData.get("items"));
            response.put("customerName", authResult.getCustomerName());
            response.put("message", "주문 생성 및 결제 페이지 데이터 생성 성공");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "주문 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @Operation(summary = "포인트 충전 페이지", description = "포인트 충전을 위한 결제 페이지를 표시합니다.")
    @GetMapping("/charge")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPaymentPage(@RequestParam(required = false) String userId,
                                 @RequestParam(required = false) Integer amount) {

        Map<String, Object> response = new HashMap<>();
        
        var authResult = paymentAuthenticationService.validateCustomerForPointCharge();
        
        if (authResult.isSuccess()) {
            userId = authResult.getCustomerIdAsString();
        } else {
            if (userId == null) return ResponseEntity.badRequest()
                    .body(Map.of("error", "포인트 충전을 위해서는 로그인이 필요합니다."));
        }
        
        if (amount == null) return ResponseEntity.badRequest()
                .body(Map.of("error", "충전 금액을 입력해주세요."));

        if (amount <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "충전 금액은 0보다 커야 합니다."));
        }
        
        String orderId = "ORDER_" + UUID.randomUUID().toString().substring(0, 12);

        response.put("userId", userId);
        response.put("clientKey", clientKey);
        response.put("amount", amount);
        response.put("orderId", orderId);
        response.put("authenticated", authResult.isSuccess());
        response.put("message", authResult.isSuccess() ? 
                    "포인트 충전 페이지 데이터 생성 성공" : 
                    authResult.getErrorMessage());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 성공 콜백", description = "토스페이먼츠 결제 성공 콜백을 처리합니다.")
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam Integer amount,
                                 Model model) {

        try {
            var validationResult = paymentValidationService.validateCallback(orderId, amount, paymentKey);

            if (!validationResult.isSuccess()) {
                model.addAttribute("error", validationResult.getErrorMessage());
                return "payment/fail";
            }

            TossPaymentResponse tossResponse = tossPaymentService.confirmPayment(paymentKey, orderId, amount);

            PaymentRequest paymentRequest = validationResult.getPaymentRequest();
            UUID internalOrderId = paymentRequest.getOrderId();
            
            Order order = orderService.findById(internalOrderId)
                    .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다."));
            
            Customer customer = customerRepository.findById(order.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다."));
            
            Payment savedPayment = paymentService.saveSuccessfulPayment(paymentRequest, customer, tossResponse);
            
            orderService.completePayment(internalOrderId, savedPayment.getPaymentId());
            
            paymentValidationService.updatePaymentStatus(paymentRequest.getPaymentRequestId(), "COMPLETED");

            model.addAttribute("paymentKey", paymentKey);
            model.addAttribute("orderId", orderId);  // 토스 주문 ID
            model.addAttribute("internalOrderId", internalOrderId.toString());  // 내부 주문 ID
            model.addAttribute("amount", amount);
            model.addAttribute("status", tossResponse.getStatus());
            model.addAttribute("method", tossResponse.getMethod());
            model.addAttribute("approvedAt", tossResponse.getApprovedAt());
            model.addAttribute("message", "결제가 성공적으로 처리되었습니다.");

            return "payment/success";

        } catch (Exception e) {
            try {
                var validationResult = paymentValidationService.validateCallback(orderId, amount, paymentKey);
                if (validationResult.isSuccess()) {
                    PaymentRequest paymentRequest = validationResult.getPaymentRequest();
                    paymentValidationService.updatePaymentStatus(paymentRequest.getPaymentRequestId(), "FAILED");

                    orderService.cancelOrder(paymentRequest.getOrderId());

                    try {
                        Order order = orderService.findById(paymentRequest.getOrderId())
                                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다."));
                        Customer customer = customerRepository.findById(order.getCustomerId())
                                .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다."));
                        var usedPoints = paymentRequest.getUsedPoints();
                        pointService.refundPoints(customer.getId(), usedPoints);
                    } catch (Exception pointRollbackException) {
                        System.err.println("포인트 롤백 처리 실패: " + pointRollbackException.getMessage());
                    }
                }
            } catch (Exception rollbackException) {
                System.err.println("롤백 처리 실패: " + rollbackException.getMessage());
            }

            model.addAttribute("error", "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
            return "payment/fail";
        }
    }

    @Operation(summary = "결제 취소 콜백", description = "토스페이먼츠 결제 취소 콜백을 처리합니다.")
    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam(required = false) String paymentKey,
                               @RequestParam(required = false) String orderId,
                               @RequestParam(required = false) Integer amount,
                               @RequestParam(required = false) String message,
                               @RequestParam(required = false) String code,
                               Model model) {

        if (orderId != null) {
            try {
                Optional<PaymentRequest> savedRequest = paymentValidationService.findByTossOrderId(orderId);
                
                if (savedRequest.isPresent()) {
                    PaymentRequest paymentRequest = savedRequest.get();

                    paymentValidationService.updatePaymentStatus(paymentRequest.getPaymentRequestId(), "CANCELED");
                    
                    orderService.cancelOrder(paymentRequest.getOrderId());
                    
                    try {
                        Order order = orderService.findById(paymentRequest.getOrderId())
                                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다."));
                        
                        Customer customer = customerRepository.findById(order.getCustomerId())
                                .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다."));
                        
                        Integer usedPoints = paymentRequest.getUsedPoints();
                        if (usedPoints != null && usedPoints > 0) {
                            pointService.refundPoints(customer.getId(), usedPoints);
                        }
                        
                    } catch (Exception pointRollbackException) {
                        System.err.println("포인트 롤백 처리 실패: " + pointRollbackException.getMessage());
                    }
                    
                    model.addAttribute("internalOrderId", paymentRequest.getOrderId().toString());
                    model.addAttribute("rollbackCompleted", true);
                } else {
                    model.addAttribute("rollbackCompleted", false);
                    model.addAttribute("rollbackError", "결제 요청을 찾을 수 없습니다.");
                }
                
            } catch (Exception e) {
                // 롤백 실패시 로그 기록
                System.err.println("결제 취소 롤백 처리 실패: " + e.getMessage());
                model.addAttribute("rollbackCompleted", false);
                model.addAttribute("rollbackError", e.getMessage());
            }
        }

        model.addAttribute("message", message != null ? message : "결제가 취소되었습니다.");
        model.addAttribute("code", code);
        model.addAttribute("orderId", orderId);
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("amount", amount);
        model.addAttribute("status", "CANCELED");

        return "payment/cancel";
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

        if (orderId != null) {
            try {
                Optional<PaymentRequest> savedRequest = paymentValidationService.findByTossOrderId(orderId);
                
                if (savedRequest.isPresent()) {
                    PaymentRequest paymentRequest = savedRequest.get();
                    
                    paymentValidationService.updatePaymentStatus(paymentRequest.getPaymentRequestId(), "FAILED");
                    
                    orderService.cancelOrder(paymentRequest.getOrderId());
                    
                    try {
                        Order order = order
                                .findById(paymentRequest.getOrderId())
                                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다."));
                        Customer customer = customerRepository.findById(order.getCustomerId())
                                .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다."));
                        Integer usedPoints = paymentRequest.getUsedPoints();
                        if (usedPoints != null && usedPoints > 0) {
                            pointService.refundPoints(customer.getId(), usedPoints);
                        }
                    } catch (Exception pointRollbackException) {
                        System.err.println("포인트 롤백 처리 실패: " + pointRollbackException.getMessage());
                    }
                    
                    model.addAttribute("internalOrderId", paymentRequest.getOrderId().toString());
                    model.addAttribute("rollbackCompleted", true);
                } else {
                    model.addAttribute("rollbackCompleted", false);
                    model.addAttribute("rollbackError", "결제 요청을 찾을 수 없습니다.");
                }
                
            } catch (Exception e) {
                System.err.println("결제 실패 롤백 처리 실패: " + e.getMessage());
                model.addAttribute("rollbackCompleted", false);
                model.addAttribute("rollbackError", e.getMessage());
            }
        }

        model.addAttribute("message", message != null ? message : "알 수 없는 오류가 발생했습니다.");
        model.addAttribute("code", code);
        model.addAttribute("orderId", orderId);
        model.addAttribute("status", "FAILED");

        return "payment/fail";
    }
}