package profect.eatcloud.Domain.Store.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Store.Repository.MenuRepository;
import profect.eatcloud.Domain.Store.Repository.StoreRepository;

import java.util.List;
import java.util.UUID;

// service/MenuService.java
@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    /*public Menu createMenu(UUID storeId, MenuRequestDto dto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Menu menu = Menu.builder()
                .store(store)
                .menuNum(dto.getMenuNum())
                .menuName(dto.getMenuName())
                .menuCategoryCode(dto.getMenuCategoryCode())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .isAvailable(dto.isAvailable())
                .imageUrl(dto.getImageUrl())
                .build();

        return menuRepository.save(menu);
    }*/

    public List<Menu> getMenusByStore(UUID storeId) {
        return menuRepository.findByStoreStoreId(storeId);
    }

    public Menu updateMenu(UUID menuId, MenuRequestDto dto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        menu.updateFromDto(dto); // Menu 엔티티에 updateFromDto 메서드 추가 필요
        return menuRepository.save(menu);
    }

    public void deleteMenu(UUID menuId) {
        menuRepository.deleteById(menuId);
    }
}
