package profect.eatcloud.Domain.Admin.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.Store.Entity.PCategory;
import java.util.UUID;

public interface AdminCategoryRepository extends JpaRepository<PCategory, UUID> {
} 