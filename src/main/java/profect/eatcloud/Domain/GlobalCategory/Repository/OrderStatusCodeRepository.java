package profect.eatcloud.Domain.GlobalCategory.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderStatusCode;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;

@Repository
public interface OrderStatusCodeRepository extends BaseTimeRepository<OrderStatusCode, String> {
    Optional<OrderStatusCode> findByCode(String code);
}