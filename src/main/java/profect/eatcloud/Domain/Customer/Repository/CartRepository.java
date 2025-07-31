package profect.eatcloud.Domain.Customer.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.Customer.Entity.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByCustomerId(UUID customerId);
    void deleteByCustomerId(UUID customerId);
}
