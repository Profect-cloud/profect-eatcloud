package profect.eatcloud.Domain.Order.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Order.Service.OrderService;
import profect.eatcloud.Domain.Order.Entity.Order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "8. Order Management")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 조회", description = "주문 ID로 주문 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable UUID orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Order> order = orderService.findById(orderId);

            if (order.isPresent()) {
                Order foundOrder = order.get();
                response.put("orderId", foundOrder.getOrderId());
                response.put("orderNumber", foundOrder.getOrderNumber());
                response.put("customerId", foundOrder.getCustomerId());
                response.put("storeId", foundOrder.getStoreId());
                response.put("paymentId", foundOrder.getPaymentId());
                response.put("orderMenuList", foundOrder.getOrderMenuList());
                response.put("orderStatus", foundOrder.getOrderStatusCode().getCode());
                response.put("orderType", foundOrder.getOrderTypeCode().getCode());
                response.put("createdAt", foundOrder.getTimeData().getCreatedAt());
                response.put("message", "주문 조회 성공");

                return ResponseEntity.ok(response);
            } else {
                response.put("error", "주문을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }

            
        } catch (Exception e) {
            response.put("error", "주문 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "주문 번호로 주문 조회", description = "주문 번호로 주문 정보를 조회합니다.")
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getOrderByNumber(@PathVariable String orderNumber) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Order> order = orderService.findOrderByNumber(orderNumber);
            
            if (order.isPresent()) {
                Order foundOrder = order.get();
                response.put("orderId", foundOrder.getOrderId());
                response.put("orderNumber", foundOrder.getOrderNumber());
                response.put("customerId", foundOrder.getCustomerId());
                response.put("storeId", foundOrder.getStoreId());
                response.put("paymentId", foundOrder.getPaymentId());
                response.put("orderMenuList", foundOrder.getOrderMenuList());
                response.put("orderStatus", foundOrder.getOrderStatusCode().getCode());
                response.put("orderType", foundOrder.getOrderTypeCode().getCode());
                response.put("createdAt", foundOrder.getTimeData().getCreatedAt());
                response.put("message", "주문 조회 성공");
                
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "주문을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("error", "주문 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}