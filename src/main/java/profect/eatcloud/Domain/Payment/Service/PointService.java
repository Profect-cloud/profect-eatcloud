package profect.eatcloud.Domain.Payment.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final CustomerRepository customerRepository;

    /**
     * 포인트 사용 가능 여부 확인
     */
    public boolean canUsePoints(UUID customerId, Integer pointsToUse) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            return false;
        }

        Integer currentPoints = customer.get().getPoints();
        return currentPoints != null && currentPoints >= pointsToUse;
    }

    /**
     * 포인트 차감
     */
    public PointResult usePoints(UUID customerId, Integer pointsToUse) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return PointResult.fail("고객을 찾을 수 없습니다");
        }

        Customer customer = customerOpt.get();
        Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;

        if (currentPoints < pointsToUse) {
            return PointResult.fail("포인트가 부족합니다. 보유: " + currentPoints + ", 사용요청: " + pointsToUse);
        }

        // 포인트 차감
        customer.setPoints(currentPoints - pointsToUse);
        customerRepository.save(customer);

        return PointResult.success(pointsToUse, currentPoints - pointsToUse);
    }

    /**
     * 포인트 적립 (환불시 사용)
     */
    public PointResult refundPoints(UUID customerId, Integer pointsToRefund) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return PointResult.fail("고객을 찾을 수 없습니다");
        }

        Customer customer = customerOpt.get();
        Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;

        // 포인트 적립
        customer.setPoints(currentPoints + pointsToRefund);
        customerRepository.save(customer);

        return PointResult.success(pointsToRefund, currentPoints + pointsToRefund);
    }

    /**
     * 포인트 처리 결과 클래스
     */
    @Getter
    public static class PointResult {
        // getters
        private final boolean success;
        private final String errorMessage;
        private final Integer usedPoints;
        private final Integer remainingPoints;

        private PointResult(boolean success, String errorMessage, Integer usedPoints, Integer remainingPoints) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.usedPoints = usedPoints;
            this.remainingPoints = remainingPoints;
        }

        public static PointResult success(Integer usedPoints, Integer remainingPoints) {
            return new PointResult(true, null, usedPoints, remainingPoints);
        }

        public static PointResult fail(String errorMessage) {
            return new PointResult(false, errorMessage, 0, 0);
        }

    }
}