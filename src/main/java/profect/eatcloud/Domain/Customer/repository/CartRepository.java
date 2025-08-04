package profect.eatcloud.Domain.Customer.repository;

import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import profect.eatcloud.Domain.Customer.Entity.Cart;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends BaseTimeRepository<Cart, UUID> {
    Optional<Cart> findByCustomerId(UUID customerId);
    void deleteByCustomerId(UUID customerId);

    @Query("SELECT COUNT(c) > 0 FROM Cart c WHERE c.customer.id = :customerId")
    boolean existsByCustomerId(@Param("customerId") UUID customerId);
}
