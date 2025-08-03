package profect.eatcloud.Domain.Order.Service;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Domain.Order.Repository.OrderRepository;
import profect.eatcloud.Domain.Order.Dto.OrderMenu;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderStatusCode;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderTypeCode;
import profect.eatcloud.Domain.GlobalCategory.Repository.OrderStatusCodeRepository;
import profect.eatcloud.Domain.GlobalCategory.Repository.OrderTypeCodeRepository;
import profect.eatcloud.Domain.Customer.Service.CartService;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Repository.MenuRepository_min;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusCodeRepository orderStatusCodeRepository;
    private final OrderTypeCodeRepository orderTypeCodeRepository;
    private final CartService cartService;
    private final MenuRepository_min menuRepository;

    public Order createPendingOrder(UUID customerId, UUID storeId, List<OrderMenu> orderMenuList, String orderType,
                                   Boolean usePoints, Integer pointsToUse) {
        String orderNumber = generateOrderNumber();

        OrderStatusCode statusCode = orderStatusCodeRepository.findByCode("PENDING")
                .orElseThrow(() -> new RuntimeException("주문 상태 코드를 찾을 수 없습니다: PENDING"));
        
        OrderTypeCode typeCode = orderTypeCodeRepository.findByCode(orderType)
                .orElseThrow(() -> new RuntimeException("주문 타입 코드를 찾을 수 없습니다: " + orderType));

        for (OrderMenu orderMenu : orderMenuList) {
            Menu menu = menuRepository.findById(orderMenu.getMenuId())
                    .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + orderMenu.getMenuId()));
            orderMenu.setPrice(menu.getPrice().intValue());
        }

        Integer totalPrice = calculateTotalAmount(orderMenuList);

        if (usePoints == null) {
            usePoints = false;
        }
        if (pointsToUse == null) {
            pointsToUse = 0;
        }

        Integer finalPaymentAmount = Math.max(totalPrice - pointsToUse, 0);

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .orderMenuList(orderMenuList)
                .customerId(customerId)
                .storeId(storeId)
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

    @Transactional(readOnly = true)
    public Optional<Order> findOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public void completePayment(UUID orderId, UUID paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        OrderStatusCode paidStatus = orderStatusCodeRepository.findByCode("PAID")
                .orElseThrow(() -> new RuntimeException("주문 상태 코드를 찾을 수 없습니다: PAID"));

        order.setPaymentId(paymentId);
        order.setOrderStatusCode(paidStatus);

        orderRepository.save(order);

        try {
            cartService.invalidateCartAfterOrder(order.getCustomerId());
            log.info("Cart invalidated after successful payment for customer: {}, order: {}",
                    order.getCustomerId(), order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to invalidate cart after payment completion for customer: {}, order: {}",
                    order.getCustomerId(), order.getOrderNumber(), e);
        }
    }

    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        OrderStatusCode canceledStatus = orderStatusCodeRepository.findByCode("CANCELED")
                .orElseThrow(() -> new RuntimeException("주문 상태 코드를 찾을 수 없습니다: CANCELED"));
        order.setOrderStatusCode(canceledStatus);

        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Integer calculateTotalAmount(List<OrderMenu> orderMenuList) {
        return orderMenuList.stream()
                .mapToInt(menu -> menu.getPrice() * menu.getQuantity())
                .sum();
    }

    private String generateOrderNumber() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "ORD-" + date + "-" + randomPart;
    }

    public List<Order> findOrdersByCustomer(UUID customerId) {
        return orderRepository.findAllByCustomerId(customerId);
    }

    public Order findOrderByCustomerAndOrderId(UUID customerId, UUID orderId) {
        return orderRepository.findByOrderIdAndCustomerIdAndTimeData_DeletedAtIsNull(orderId, customerId)
                .orElseThrow(() -> new RuntimeException("해당 주문이 없습니다."));
    }

    public List<Order> findOrdersByStore(UUID storeId) {
        return orderRepository.findAllByStoreId(storeId);
    }

    public Order findOrderByStoreAndOrderId(UUID storeId, UUID orderId) {
        return orderRepository.findByOrderIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new RuntimeException("해당 매장에 주문이 없습니다."));
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, String statusCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));

        OrderStatusCode statusCodeEntity = orderStatusCodeRepository.findById(statusCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상태 코드입니다."));

        order.setOrderStatusCode(statusCodeEntity);
        // 변경 감지로 자동 업데이트
    }
}