package profect.eatcloud.Domain.Customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerOrderResponse;
import profect.eatcloud.Domain.Customer.service.CustomerOrderService;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Domain.Order.Service.OrderService;
import profect.eatcloud.Security.SecurityUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/orders")
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "2. CustomerOrderController", description = "고객 주문 생성 API")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;
    private final OrderService orderService;

    public CustomerOrderController(OrderService orderService, CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
        this.orderService = orderService;
    }

    private UUID getCustomerUuid(@AuthenticationPrincipal UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }

    @Operation(summary = "고객 주문 생성", description = "고객의 장바구니에서 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<CustomerOrderResponse> createOrder(
            @RequestParam String orderTypeCode,
            @RequestParam(required = false, defaultValue = "false") Boolean usePoints,
            @RequestParam(required = false, defaultValue = "0") Integer pointsToUse) {
        
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);

        Order order = customerOrderService.createOrder(customerId, orderTypeCode, usePoints, pointsToUse);
        CustomerOrderResponse dto = new CustomerOrderResponse(order, usePoints, pointsToUse);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "고객 주문 목록 조회")
    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        UUID customerId = getCustomerUuid(userDetails);
        return ResponseEntity.ok(orderService.findOrdersByCustomer(customerId));
    }

    @Operation(summary = "고객 주문 상세 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getMyOrder(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable UUID orderId) {
        UUID customerId = getCustomerUuid(userDetails);
        return ResponseEntity.ok(orderService.findOrderByCustomerAndOrderId(customerId, orderId));
    }
}
