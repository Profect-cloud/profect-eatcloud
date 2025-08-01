package profect.eatcloud.Domain.Customer.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerOrderResponse;
import profect.eatcloud.Domain.Customer.Service.CustomerOrderService;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Security.SecurityUtil;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/orders")
@RequiredArgsConstructor
@Tag(name = "2. CustomerOrderController", description = "고객 주문 생성 API")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

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
}
