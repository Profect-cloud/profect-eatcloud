package profect.eatcloud.Domain.GlobalCategory.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderTypeCode;

import java.util.Optional;

@Repository
public interface OrderTypeCodeRepository extends JpaRepository<OrderTypeCode, String> {
    Optional<OrderTypeCode> findByCode(String code);
}