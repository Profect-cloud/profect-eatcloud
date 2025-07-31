package profect.eatcloud.Domain.Order.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

@Repository
public interface OrderRepository extends BaseTimeRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);

}