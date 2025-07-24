package profect.eatcloud.Domain.Order.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Order.Dto.OrderRequestDto;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    // private final OrderService orderService; // 추후 구현 예정

    @PostMapping("/pre-order")
    public ResponseEntity<?> createPreOrder(@RequestBody PreOrderRequestDto dto) {
        // 주문 생성 후 응답 반환
        return ResponseEntity.ok("Order created (service 미구현)");
    }

} 