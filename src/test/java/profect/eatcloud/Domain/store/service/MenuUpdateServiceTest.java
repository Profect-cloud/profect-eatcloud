package profect.eatcloud.Domain.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Store.Repository.MenuRepository_min;
import profect.eatcloud.Domain.Store.Repository.StoreRepository_min;
import profect.eatcloud.Domain.Store.Service.MenuService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuUpdateServiceTest {

    private MenuService menuService;
    private MenuRepository_min menuRepository;
    private StoreRepository_min storeRepository;

    @BeforeEach
    void setUp() {
        menuRepository = mock(MenuRepository_min.class);
        storeRepository = mock(StoreRepository_min.class);
        menuService = new MenuService(menuRepository, storeRepository);
    }

    @Test
    void 존재하지_않는_메뉴일_경우_예외_발생() {
        // given
        UUID menuId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        MenuRequestDto dto = new MenuRequestDto();

        // when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            menuService.updateMenu(storeId, menuId, dto);
        });

        assertEquals("존재하지 않는 메뉴입니다", e.getMessage());
    }

    @Test
    void 메뉴_수정이_정상적으로_동작한다() {
        // given
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        Store store = Store.builder().storeId(storeId).build();

        Menu existingMenu = Menu.builder()
                .id(menuId)
                .store(store)
                .menuName("기존메뉴")
                .price(BigDecimal.valueOf(10000))
                .menuNum(1)
                .isAvailable(true)
                .build();

        MenuRequestDto updateDto = new MenuRequestDto();
        updateDto.setMenuName("업데이트된메뉴");
        updateDto.setPrice(BigDecimal.valueOf(15000));
        updateDto.setIsAvailable(false);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(existingMenu));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // when
        menuService.updateMenu(storeId, menuId, updateDto);

        // then
        assertEquals("업데이트된메뉴", existingMenu.getMenuName());
        assertEquals(BigDecimal.valueOf(15000), existingMenu.getPrice());
        assertFalse(existingMenu.getIsAvailable());
    }

    @Test
    void 메뉴_이름이_null이면_예외() {
        // given
        UUID menuId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Store store = Store.builder().storeId(storeId).build();
        Menu menu = Menu.builder().id(menuId).store(store).build();

        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuName(null);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            menuService.updateMenu(storeId, menuId, dto);
        });

        assertEquals("메뉴 이름은 필수입니다", e.getMessage());
    }

    // 더 추가할 수 있는 테스트 항목들:
    // - 가격이 음수면 예외
    @Test
    void 가격이_음수면_예외발생() {
        // given
        UUID menuId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Store store = Store.builder().storeId(storeId).build();
        Menu menu = Menu.builder().id(menuId).store(store).build();

        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuName("된장찌개");
        dto.setPrice(new BigDecimal("-1000")); // 음수

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            menuService.updateMenu(storeId, menuId, dto);
        });

        assertEquals("가격은 0 이상이어야 합니다.", e.getMessage());
    }

    // - menuNum 변경 시 중복 검사 (정책 적용 시)
    @Test
    void menuNum_중복되면_예외발생() {
        // given
        UUID menuId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Store store = Store.builder().storeId(storeId).build();
        Menu menu = Menu.builder().id(menuId).store(store).menuNum(1).build();

        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuName("비빔밥");
        dto.setPrice(BigDecimal.valueOf(10000));
        dto.setMenuNum(2); // menuNum 변경

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.existsByStoreAndMenuNum(store, 2)).thenReturn(true); // 이미 존재하는 번호

        // when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            menuService.updateMenu(storeId, menuId, dto);
        });

        assertEquals("해당 메뉴 번호는 이미 존재합니다", e.getMessage());
    }


    @Test
    void 전체_필드_수정_정상작동() {
        // given
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        Store store = Store.builder().storeId(storeId).build();
        Menu menu = Menu.builder()
                .id(menuId)
                .store(store)
                .menuName("기존이름")
                .menuNum(1)
                .menuCategoryCode("KOREAN")
                .price(BigDecimal.valueOf(10000))
                .description("기존 설명")
                .isAvailable(true)
                .imageUrl("old-url.png")
                .build();

        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuName("신규이름");
        dto.setMenuNum(2);
        dto.setMenuCategoryCode("CHINESE");
        dto.setPrice(BigDecimal.valueOf(14000));
        dto.setDescription("신규 설명");
        dto.setImageUrl("new-url.jpg");
        dto.setIsAvailable(false);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.existsByStoreAndMenuNum(store, 2)).thenReturn(false);

        // when
        menuService.updateMenu(storeId, menuId, dto);

        // then
        assertEquals("신규이름", menu.getMenuName());
        assertEquals(2, menu.getMenuNum());
        assertEquals("CHINESE", menu.getMenuCategoryCode());
        assertEquals(BigDecimal.valueOf(14000), menu.getPrice());
        assertEquals("신규 설명", menu.getDescription());
        assertEquals("new-url.jpg", menu.getImageUrl());
        assertFalse(menu.getIsAvailable());
    }

}
