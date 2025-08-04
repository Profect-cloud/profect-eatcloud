package profect.eatcloud.domain.GlobalCategory.Repository;

import org.springframework.stereotype.Repository;
import profect.eatcloud.domain.GlobalCategory.Entity.PaymentStatusCode;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;

@Repository
public interface PaymentStatusCodeRepository extends BaseTimeRepository<PaymentStatusCode, String> {
    Optional<PaymentStatusCode> findByCode(String code);
}