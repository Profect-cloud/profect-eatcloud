package profect.eatcloud.domain.Payment.Repository;

import org.springframework.stereotype.Repository;
import profect.eatcloud.domain.Payment.Entity.PaymentRequest;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.UUID;

@Repository
public interface PaymentRequestRepository extends BaseTimeRepository<PaymentRequest, UUID> {
}