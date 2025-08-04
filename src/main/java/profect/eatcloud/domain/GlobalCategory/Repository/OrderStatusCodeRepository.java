package profect.eatcloud.domain.GlobalCategory.Repository;

import org.springframework.stereotype.Repository;
import profect.eatcloud.domain.GlobalCategory.Entity.OrderStatusCode;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;

@Repository
public interface OrderStatusCodeRepository extends BaseTimeRepository<OrderStatusCode, String> {
    Optional<OrderStatusCode> findByCode(String code);
}