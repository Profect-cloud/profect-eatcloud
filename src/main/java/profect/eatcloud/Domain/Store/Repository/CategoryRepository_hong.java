package profect.eatcloud.Domain.Store.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import profect.eatcloud.Domain.Store.Entity.Menu;

public interface CategoryRepository_hong extends JpaRepository<Menu, UUID> {
}
