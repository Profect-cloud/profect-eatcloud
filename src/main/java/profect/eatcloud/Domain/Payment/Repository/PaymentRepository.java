package profect.eatcloud.Domain.Payment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.Payment.Entity.Payment;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends BaseTimeRepository<Payment, UUID> {
    
}