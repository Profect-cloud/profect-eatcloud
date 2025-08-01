package profect.eatcloud.Domain.Customer.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerOrderResponse;
import profect.eatcloud.Domain.Customer.Service.CustomerOrderService;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Security.SecurityUtil;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/order")
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
}
