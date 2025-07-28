package profect.eatcloud.Domain.Store.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Store.Repository.MenuRepository_min;
import profect.eatcloud.Domain.Store.Repository.StoreRepository_min;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository_min menuRepository;
    private final StoreRepository_min storeRepository;

    public Menu createMenu(UUID storeId, MenuRequestDto dto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Menu menu = Menu.builder()
                .store(store)
                .menuNum(dto.getMenuNum())
                .menuName(dto.getMenuName())
                .menuCategoryCode(dto.getMenuCategoryCode())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .isAvailable(dto.getIsAvailable())
                .imageUrl(dto.getImageUrl())
                .build();

        return menuRepository.save(menu);
    }

    public Menu updateMenu(UUID menuId, MenuRequestDto dto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        menu.updateFrom(dto); // 도메인 메서드 호출

        return menuRepository.save(menu);
    }


    public List<Menu> getMenusByStore(UUID storeId) {
        return menuRepository.findByStoreStoreId(storeId);
    }

    public Menu getMenu(UUID menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
    }

    public void deleteMenu(UUID menuId) {
        menuRepository.deleteById(menuId);
    }

    //public Menu requestAIDescription(UUID menuId) {requestAIDescription()}
}

