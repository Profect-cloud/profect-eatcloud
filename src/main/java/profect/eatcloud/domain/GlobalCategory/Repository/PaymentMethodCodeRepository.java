package profect.eatcloud.domain.GlobalCategory.Repository;

import org.springframework.stereotype.Repository;
import profect.eatcloud.domain.GlobalCategory.Entity.PaymentMethodCode;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;

@Repository
public interface PaymentMethodCodeRepository extends BaseTimeRepository<PaymentMethodCode, String> {
    Optional<PaymentMethodCode> findByCode(String code);
}