package profect.eatcloud.domain.GlobalCategory.Repository;

import org.springframework.stereotype.Repository;
import profect.eatcloud.domain.GlobalCategory.Entity.OrderTypeCode;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;

@Repository
public interface OrderTypeCodeRepository extends BaseTimeRepository<OrderTypeCode, String> {
    Optional<OrderTypeCode> findByCode(String code);
}