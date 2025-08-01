package profect.eatcloud.Domain.Customer.Controller;

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
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<CustomerOrderResponse> createOrder(@RequestParam String orderTypeCode) {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);

        Order order = customerOrderService.createOrder(customerId, orderTypeCode);
        CustomerOrderResponse dto = new CustomerOrderResponse(order);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/with-points")
    public ResponseEntity<CustomerOrderResponse> createOrderWithPoints(
            @RequestParam String orderTypeCode,
            @RequestParam(required = false, defaultValue = "false") Boolean usePoints,
            @RequestParam(required = false, defaultValue = "0") Integer pointsToUse) {
        
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);

        Order order = customerOrderService.createOrderWithPoints(customerId, orderTypeCode, usePoints, pointsToUse);
        CustomerOrderResponse dto = new CustomerOrderResponse(order, usePoints, pointsToUse);
        return ResponseEntity.ok(dto);
    }
}
