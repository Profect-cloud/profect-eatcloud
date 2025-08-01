package profect.eatcloud.Domain.GlobalCategory.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.GlobalCategory.Entity.PaymentStatusCode;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;

@Repository
public interface PaymentStatusCodeRepository extends BaseTimeRepository<PaymentStatusCode, String> {
    Optional<PaymentStatusCode> findByCode(String code);
}