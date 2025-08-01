package profect.eatcloud.Domain.store.service;

import org.junit.jupiter.api.Test;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Store.Repository.MenuRepository_min;
import profect.eatcloud.Domain.Store.Repository.StoreRepository_min;
import profect.eatcloud.Domain.Store.Service.MenuService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

class MenuQueryServiceTest {

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
    void 가게별_메뉴_전체조회_성공() {
        UUID storeId = UUID.randomUUID();
        Store store = Store.builder().storeId(storeId).build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findAllByStoreAndTimeData_DeletedAtIsNull(store))
                .thenReturn(List.of(new Menu(), new Menu()));

        List<Menu> result = menuService.getMenusByStore(storeId);

        assertEquals(2, result.size());
    }

    @Test
    void 가게별_메뉴_전체조회_가게없으면_예외() {
        UUID storeId = UUID.randomUUID();
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            menuService.getMenusByStore(storeId);
        });

        assertEquals("존재하지 않는 매장입니다", e.getMessage());
    }

    @Test
    void 단일메뉴조회_성공() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        Store store = Store.builder().storeId(storeId).build();
        Menu menu = Menu.builder().id(menuId).store(store).build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findByIdAndStoreAndTimeData_DeletedAtIsNull(menuId, store))
                .thenReturn(Optional.of(menu));

        Menu result = menuService.getMenuById(storeId, menuId);

        assertEquals(menuId, result.getId());
    }

    @Test
    void 단일메뉴조회_가게없으면_예외() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();

        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            menuService.getMenuById(storeId, menuId);
        });

        assertEquals("존재하지 않는 매장입니다", e.getMessage());
    }

    @Test
    void 단일메뉴조회_메뉴없으면_예외() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        Store store = Store.builder().storeId(storeId).build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findByIdAndStoreAndTimeData_DeletedAtIsNull(menuId, store))
                .thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            menuService.getMenuById(storeId, menuId);
        });

        assertEquals("해당 메뉴를 찾을 수 없습니다", e.getMessage());
    }
}
