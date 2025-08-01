package profect.eatcloud.Domain.Order.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Domain.Order.Repository.OrderRepository;
import profect.eatcloud.Domain.Order.Dto.AdminOrderResponseDto;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderStatusCode;
import profect.eatcloud.Domain.GlobalCategory.Repository.OrderStatusCodeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusCodeRepository orderStatusCodeRepository;

    /**
     * 주문 수락 (PAID -> CONFIRMED)
     */
    public AdminOrderResponseDto confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        // 현재 상태가 PAID인지 확인
        if (!"PAID".equals(order.getOrderStatusCode().getCode())) {
            throw new RuntimeException("결제 완료된 주문만 수락할 수 있습니다. 현재 상태: " + order.getOrderStatusCode().getCode());
        }

        // CONFIRMED 상태로 변경
        OrderStatusCode confirmedStatus = orderStatusCodeRepository.findByCode("CONFIRMED")
                .orElseThrow(() -> new RuntimeException("주문 상태 코드를 찾을 수 없습니다: CONFIRMED"));

        order.setOrderStatusCode(confirmedStatus);
        orderRepository.save(order);

        return AdminOrderResponseDto.builder()
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .orderStatus("CONFIRMED")
                .message("주문이 수락되었습니다.")
                .build();
    }

    /**
     * 주문 완료 (CONFIRMED -> COMPLETED)
     */
    public AdminOrderResponseDto completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        // 현재 상태가 CONFIRMED인지 확인
        if (!"CONFIRMED".equals(order.getOrderStatusCode().getCode())) {
            throw new RuntimeException("수락된 주문만 완료할 수 있습니다. 현재 상태: " + order.getOrderStatusCode().getCode());
        }

        // COMPLETED 상태로 변경
        OrderStatusCode completedStatus = orderStatusCodeRepository.findByCode("COMPLETED")
                .orElseThrow(() -> new RuntimeException("주문 상태 코드를 찾을 수 없습니다: COMPLETED"));

        order.setOrderStatusCode(completedStatus);
        orderRepository.save(order);

        return AdminOrderResponseDto.builder()
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .orderStatus("COMPLETED")
                .message("주문이 완료되었습니다.")
                .build();
    }

    /**
     * 주문 상태 조회
     */
    @Transactional(readOnly = true)
    public AdminOrderResponseDto getOrderStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        return AdminOrderResponseDto.builder()
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatusCode().getCode())
                .message("주문 상태 조회 완료")
                .build();
    }
}
