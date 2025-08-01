package profect.eatcloud.Domain.Order.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Domain.Order.Repository.OrderRepository;
import profect.eatcloud.Domain.Order.Dto.OrderMenu;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderStatusCode;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderTypeCode;
import profect.eatcloud.Domain.GlobalCategory.Repository.OrderStatusCodeRepository;
import profect.eatcloud.Domain.GlobalCategory.Repository.OrderTypeCodeRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusCodeRepository orderStatusCodeRepository;
    private final OrderTypeCodeRepository orderTypeCodeRepository;

    /**
     * 주문 생성 (결제 전 단계)
     */
    public Order createPendingOrder(UUID customerId, UUID storeId, List<OrderMenu> orderMenuList, String orderType) {
        return createPendingOrder(customerId, storeId, orderMenuList, orderType, null, null, null, null);
    }

    /**
     * 주문 생성 (결제 전 단계) - 포인트 정보 포함
     */
    public Order createPendingOrder(UUID customerId, UUID storeId, List<OrderMenu> orderMenuList, String orderType, 
                                   Integer totalPrice, Boolean usePoints, Integer pointsToUse, Integer finalPaymentAmount) {
        // 주문번호 생성
        String orderNumber = generateOrderNumber();
        
        // 주문 상태 및 타입 조회
        OrderStatusCode statusCode = orderStatusCodeRepository.findByCode("PENDING")
                .orElseThrow(() -> new RuntimeException("주문 상태 코드를 찾을 수 없습니다: PENDING"));
        
        OrderTypeCode typeCode = orderTypeCodeRepository.findByCode(orderType)
                .orElseThrow(() -> new RuntimeException("주문 타입 코드를 찾을 수 없습니다: " + orderType));

        // 기본값 설정
        if (totalPrice == null) {
            totalPrice = calculateTotalAmount(orderMenuList);
        }
        if (usePoints == null) {
            usePoints = false;
        }
        if (pointsToUse == null) {
            pointsToUse = 0;
        }
        if (finalPaymentAmount == null) {
            finalPaymentAmount = totalPrice - pointsToUse;
        }

        // 주문 생성
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerId(customerId)
                .storeId(storeId)
                .orderMenuList(orderMenuList)
                .orderStatusCode(statusCode)
                .orderTypeCode(typeCode)
                .totalPrice(totalPrice)
                .usePoints(usePoints)
                .pointsToUse(pointsToUse)
                .finalPaymentAmount(finalPaymentAmount)
                .build();

        return orderRepository.save(order);
    }


    @Transactional(readOnly = true)
    public Optional<Order> findById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    //findOrderByNumber
    @Transactional(readOnly = true)
    public Optional<Order> findOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public void completePayment(UUID orderId, UUID paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        OrderStatusCode paidStatus = orderStatusCodeRepository.findByCode("PAID")
                .orElseThrow(() -> new RuntimeException("주문 상태 코드를 찾을 수 없습니다: PAID"));

        // 기존 엔티티를 직접 수정 (timeData 유지)
        order.setPaymentId(paymentId);
        order.setOrderStatusCode(paidStatus);

        orderRepository.save(order);
    }

    /**
     * 결제 실패 시 주문 취소
     */
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        OrderStatusCode canceledStatus = orderStatusCodeRepository.findByCode("CANCELED")
                .orElseThrow(() -> new RuntimeException("주문 상태 코드를 찾을 수 없습니다: CANCELED"));

        // 기존 엔티티를 직접 수정 (timeData 유지)
        order.setOrderStatusCode(canceledStatus);

        orderRepository.save(order);
    }

    /**
     * 주문 총 금액 계산
     */
    @Transactional(readOnly = true)
    public Integer calculateTotalAmount(List<OrderMenu> orderMenuList) {
        return orderMenuList.stream()
                .mapToInt(menu -> menu.getPrice() * menu.getQuantity())
                .sum();
    }

    /**
     * 주문 번호 생성
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}