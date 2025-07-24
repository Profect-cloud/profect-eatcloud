package profect.eatcloud.Domain.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.User.Entity.Order;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
} 