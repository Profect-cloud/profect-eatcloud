package profect.eatcloud.domain.Store.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import profect.eatcloud.domain.Store.Entity.Category;

public interface CategoryRepository_hong extends JpaRepository<Category, UUID> {
}
