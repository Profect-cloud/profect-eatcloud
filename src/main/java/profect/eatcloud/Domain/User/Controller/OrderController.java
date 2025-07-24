package profect.eatcloud.Domain.User.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage/orders")
public class OrderController {
    @GetMapping
    public void getOrders(org.springframework.security.core.Authentication authentication) {}

    @GetMapping("/{order_id}")
    public void getOrder(org.springframework.security.core.Authentication authentication, @PathVariable String order_id) {}
} 