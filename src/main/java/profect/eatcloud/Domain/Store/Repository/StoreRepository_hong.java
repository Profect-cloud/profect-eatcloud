package profect.eatcloud.Domain.Store.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.Store.Entity.Store;

import java.util.UUID;

@Repository
public interface StoreRepository_hong extends JpaRepository<Store, UUID> {

}
