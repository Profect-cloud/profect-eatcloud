package profect.eatcloud.Domain.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.User.Entity.Cart;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
} 