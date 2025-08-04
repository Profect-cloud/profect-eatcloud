package profect.eatcloud.domain.store.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import profect.eatcloud.domain.store.entity.Category;

public interface CategoryRepository_hong extends JpaRepository<Category, UUID> {
}
