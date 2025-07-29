package profect.eatcloud.Domain.Payment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.Payment.Entity.PaymentRequest;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, UUID> {

    // 주문 ID로 결제 요청 찾기
    Optional<PaymentRequest> findByOrderId(UUID orderId);

    // 상태별로 결제 요청 찾기
    Optional<PaymentRequest> findByOrderIdAndStatus(UUID orderId, String status);

    // PG사별로 결제 요청 찾기
    Optional<PaymentRequest> findByOrderIdAndPgProvider(UUID orderId, String pgProvider);
}