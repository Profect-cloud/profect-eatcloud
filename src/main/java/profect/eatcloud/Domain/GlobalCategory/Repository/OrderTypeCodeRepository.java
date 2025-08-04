package profect.eatcloud.domain.globalCategory.repository;

import org.springframework.stereotype.Repository;
import profect.eatcloud.domain.globalCategory.entity.OrderTypeCode;
import profect.eatcloud.global.timedata.BaseTimeRepository;

import java.util.Optional;

@Repository
public interface OrderTypeCodeRepository extends BaseTimeRepository<OrderTypeCode, String> {
    Optional<OrderTypeCode> findByCode(String code);
}