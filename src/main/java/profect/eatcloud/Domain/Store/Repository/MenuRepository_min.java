package profect.eatcloud.Domain.Store.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository_min extends BaseTimeRepository<Menu, UUID> {
    List<Menu> findByStoreStoreId(UUID storeId);

    boolean existsByStoreAndMenuNum(Store store, int menuNum);

    // MenuRepository_min.java

    List<Menu> findAllByStoreAndTimeData_DeletedAtIsNull(Store store);

    Optional<Menu> findByIdAndStoreAndTimeData_DeletedAtIsNull(UUID menuId, Store store);

}
