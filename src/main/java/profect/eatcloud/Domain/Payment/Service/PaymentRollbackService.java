package profect.eatcloud.Domain.Payment.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Domain.Order.Service.OrderService;
import profect.eatcloud.Domain.Payment.Entity.PaymentRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentRollbackService {
    
    private final OrderService orderService;
    private final CustomerRepository customerRepository;
    private final PointService pointService;
    private final PaymentValidationService paymentValidationService;
    
    /**
     * 결제 취소/실패 시 전체 롤백 처리
     */
    public RollbackResult rollbackPayment(PaymentRequest paymentRequest, String status) {
        try {
            log.info("결제 롤백 시작 - PaymentRequestId: {}, Status: {}", 
                    paymentRequest.getPaymentRequestId(), status);
            
            // 1. 주문 정보 조회
            Order order = orderService.findById(paymentRequest.getOrderId())
                    .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다: " + paymentRequest.getOrderId()));
            
            // 2. 고객 정보 조회
            Customer customer = customerRepository.findById(order.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다: " + order.getCustomerId()));
            
            // 3. 결제 요청 상태 업데이트
            paymentValidationService.updatePaymentStatus(paymentRequest.getPaymentRequestId(), status);
            log.info("결제 요청 상태 업데이트 완료 - Status: {}", status);
            
            // 4. 주문 취소
            orderService.cancelOrder(paymentRequest.getOrderId());
            log.info("주문 취소 완료 - OrderId: {}", paymentRequest.getOrderId());
            
            // 5. 포인트 롤백 (사용된 포인트가 있는 경우)
            if (order.getUsePoints() != null && order.getUsePoints() && 
                order.getPointsToUse() != null && order.getPointsToUse() > 0) {
                
                var pointResult = pointService.refundPoints(customer.getId(), order.getPointsToUse());
                
                if (pointResult.isSuccess()) {
                    log.info("포인트 롤백 완료 - CustomerId: {}, RefundedPoints: {}", 
                            customer.getId(), order.getPointsToUse());
                } else {
                    log.error("포인트 롤백 실패 - CustomerId: {}, Points: {}, Error: {}", 
                            customer.getId(), order.getPointsToUse(), pointResult.getErrorMessage());
                    // 포인트 롤백 실패는 전체 롤백을 실패로 처리하지 않음 (수동 처리 필요)
                }
            } else {
                log.info("사용된 포인트가 없어 포인트 롤백 생략");
            }
            
            return RollbackResult.success(paymentRequest.getOrderId(), order.getPointsToUse());
            
        } catch (Exception e) {
            log.error("결제 롤백 처리 실패 - PaymentRequestId: {}, Error: {}", 
                    paymentRequest.getPaymentRequestId(), e.getMessage(), e);
            return RollbackResult.failure(e.getMessage());
        }
    }
    
    /**
     * 주문 ID로 롤백 처리 (PaymentRequest 없이)
     */
    public RollbackResult rollbackByOrderId(UUID orderId, String status) {
        try {
            log.info("주문 ID로 롤백 시작 - OrderId: {}, Status: {}", orderId, status);
            
            // 1. 주문 정보 조회
            Order order = orderService.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다: " + orderId));
            
            // 2. 고객 정보 조회
            Customer customer = customerRepository.findById(order.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다: " + order.getCustomerId()));
            
            // 3. 주문 취소
            orderService.cancelOrder(orderId);
            log.info("주문 취소 완료 - OrderId: {}", orderId);
            
            // 4. 포인트 롤백 (사용된 포인트가 있는 경우)
            if (order.getUsePoints() != null && order.getUsePoints() && 
                order.getPointsToUse() != null && order.getPointsToUse() > 0) {
                
                var pointResult = pointService.refundPoints(customer.getId(), order.getPointsToUse());
                
                if (pointResult.isSuccess()) {
                    log.info("포인트 롤백 완료 - CustomerId: {}, RefundedPoints: {}", 
                            customer.getId(), order.getPointsToUse());
                } else {
                    log.error("포인트 롤백 실패 - CustomerId: {}, Points: {}, Error: {}", 
                            customer.getId(), order.getPointsToUse(), pointResult.getErrorMessage());
                }
            } else {
                log.info("사용된 포인트가 없어 포인트 롤백 생략");
            }
            
            return RollbackResult.success(orderId, order.getPointsToUse());
            
        } catch (Exception e) {
            log.error("주문 롤백 처리 실패 - OrderId: {}, Error: {}", orderId, e.getMessage(), e);
            return RollbackResult.failure(e.getMessage());
        }
    }
    
    /**
     * 롤백 결과를 담는 클래스
     */
    public static class RollbackResult {
        private final boolean success;
        private final UUID orderId;
        private final Integer refundedPoints;
        private final String errorMessage;
        
        private RollbackResult(boolean success, UUID orderId, Integer refundedPoints, String errorMessage) {
            this.success = success;
            this.orderId = orderId;
            this.refundedPoints = refundedPoints;
            this.errorMessage = errorMessage;
        }
        
        public static RollbackResult success(UUID orderId, Integer refundedPoints) {
            return new RollbackResult(true, orderId, refundedPoints, null);
        }
        
        public static RollbackResult failure(String errorMessage) {
            return new RollbackResult(false, null, null, errorMessage);
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public UUID getOrderId() { return orderId; }
        public Integer getRefundedPoints() { return refundedPoints != null ? refundedPoints : 0; }
        public String getErrorMessage() { return errorMessage; }
        
        public boolean hasRefundedPoints() {
            return refundedPoints != null && refundedPoints > 0;
        }
    }
}
