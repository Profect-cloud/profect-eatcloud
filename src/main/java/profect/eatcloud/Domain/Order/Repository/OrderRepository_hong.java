package profect.eatcloud.Domain.Order.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import profect.eatcloud.Domain.Store.Entity.Menu;

public interface OrderRepository_hong extends JpaRepository<Menu, UUID> {
}
