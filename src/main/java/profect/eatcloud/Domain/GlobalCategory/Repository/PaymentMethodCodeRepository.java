package profect.eatcloud.Domain.GlobalCategory.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.GlobalCategory.Entity.PaymentMethodCode;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;

@Repository
public interface PaymentMethodCodeRepository extends BaseTimeRepository<PaymentMethodCode, String> {
    Optional<PaymentMethodCode> findByCode(String code);
}