package profect.eatcloud.Domain.Admin.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.User.Entity.Order;
import java.util.UUID;

public interface AdminOrderRepository extends JpaRepository<Order, UUID> {
} 