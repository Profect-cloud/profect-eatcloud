package profect.eatcloud.Domain.Order.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.Order.Entity.Order;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
} 