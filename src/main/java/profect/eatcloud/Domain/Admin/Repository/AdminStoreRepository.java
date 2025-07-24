package profect.eatcloud.Domain.Admin.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.Store.Entity.PStore;
import java.util.UUID;

public interface AdminStoreRepository extends JpaRepository<PStore, UUID> {
} 