package profect.eatcloud.domain.Manager.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.domain.Store.Entity.Menu;
import profect.eatcloud.domain.Store.Entity.Store;
import profect.eatcloud.domain.Store.Repository.MenuRepository_min;
import profect.eatcloud.domain.Store.Repository.StoreRepository_min;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final MenuRepository_min menuRepository;
    private final StoreRepository_min storeRepository;


    public Menu createMenu(UUID storeId, MenuRequestDto dto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다"));

        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }

        if (dto.getMenuName() == null || dto.getMenuName().trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 이름은 필수입니다");
        }

        Boolean isAvailable = dto.getIsAvailable();
        if (isAvailable == null) {
            isAvailable = true;
        }

        if (menuRepository.existsByStoreAndMenuNum(store, dto.getMenuNum())) {
            throw new IllegalArgumentException("해당 메뉴 번호는 이미 존재합니다");
        }

        Menu menu = Menu.builder()
                .store(store)
                .menuNum(dto.getMenuNum())
                .menuName(dto.getMenuName())
                .menuCategoryCode(dto.getMenuCategoryCode())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .isAvailable(isAvailable)
                .imageUrl(dto.getImageUrl())
                .build();

        return menuRepository.save(menu);
    }

    public Menu updateMenu(UUID storeId, UUID menuId, MenuRequestDto dto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다"));

        // 1. 메뉴 이름 유효성
        if (dto.getMenuName() == null || dto.getMenuName().trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 이름은 필수입니다");
        }

        // 2. 가격 유효성
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }

        // 3. menuNum 중복 체크 (정책 적용 시)
        if (dto.getMenuNum() != menu.getMenuNum()) {
            boolean exists = menuRepository.existsByStoreAndMenuNum(menu.getStore(), dto.getMenuNum());
            if (exists) {
                throw new IllegalArgumentException("해당 메뉴 번호는 이미 존재합니다");
            }
        }

        // 4. isAvailable 기본값 처리
        if (dto.getIsAvailable() == null) {
            dto.setIsAvailable(true);
        }

        // 실제 업데이트
        menu.updateFrom(dto);
        return menuRepository.save(menu);
    }


    @Transactional
    public void deleteMenu(UUID menuId) {
        menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다"));

        menuRepository.deleteById(menuId); // soft delete 동작
    }
}

