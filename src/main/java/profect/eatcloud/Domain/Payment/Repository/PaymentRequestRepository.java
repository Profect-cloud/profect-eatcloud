package profect.eatcloud.Domain.Payment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.Payment.Entity.PaymentRequest;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface PaymentRequestRepository extends BaseTimeRepository<PaymentRequest, UUID> {
}