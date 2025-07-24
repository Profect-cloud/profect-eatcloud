package profect.eatcloud.Domain.Admin.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {
    @GetMapping("/{order_id}")
    public void getOrder(Authentication authentication, @PathVariable String order_id) {}

    @DeleteMapping("/{order_id}/status")
    public void deleteOrderStatus(Authentication authentication, @PathVariable String order_id) {}
} 