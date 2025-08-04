package profect.eatcloud.domain.Store.Repository;


import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import profect.eatcloud.domain.Store.Entity.Menu;
import profect.eatcloud.domain.Store.Entity.Store;
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

    @Query("SELECT DISTINCT m.store FROM Menu m WHERE m.menuCategoryCode = :code AND m.isAvailable = true")
    List<Store> findDistinctStoresByMenuCategoryCode(@Param("code") String code);
}
