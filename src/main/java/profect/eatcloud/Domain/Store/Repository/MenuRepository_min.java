package profect.eatcloud.Domain.Store.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.Store.Entity.Menu;

import java.util.List;
import java.util.UUID;

public interface MenuRepository_min extends JpaRepository<Menu, UUID> {
    List<Menu> findByStoreStoreId(UUID storeId);
}
