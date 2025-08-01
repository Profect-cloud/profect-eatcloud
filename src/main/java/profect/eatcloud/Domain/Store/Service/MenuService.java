package profect.eatcloud.Domain.Store.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Store.Repository.MenuRepository_min;
import profect.eatcloud.Domain.Store.Repository.StoreRepository_min;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository_min menuRepository;
    private final StoreRepository_min storeRepository;

    public List<Menu> getMenusByStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다"));

        return menuRepository.findAllByStoreAndTimeData_DeletedAtIsNull(store);
    }

    public Menu getMenuById(UUID storeId, UUID menuId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다"));

        return menuRepository.findByIdAndStoreAndTimeData_DeletedAtIsNull(menuId, store)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다"));
    }


}

