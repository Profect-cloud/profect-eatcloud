package profect.eatcloud.domain.store.service

import profect.eatcloud.Domain.Store.Dto.MenuRequestDto
import profect.eatcloud.Domain.Store.Entity.Menu
import profect.eatcloud.Domain.Store.Entity.Store
import profect.eatcloud.Domain.Store.Repository.MenuRepository_min
import profect.eatcloud.Domain.Store.Repository.StoreRepository_min
import profect.eatcloud.Domain.Store.Service.MenuService
import spock.lang.Specification

class MenuServiceSpec extends Specification {

    def menuRepository = Mock(MenuRepository_min)
    def storeRepository = Mock(StoreRepository_min)
    def menuService = new MenuService(menuRepository, storeRepository)

    def "메뉴를 등록할 수 있다"() {
        given:
        def storeId = UUID.randomUUID()
        def store = Store.builder().storeId(storeId).storeName("테스트가게").build()
        def dto = new MenuRequestDto(
                menuName: "치킨",
                menuCategoryCode: "chicken",
                price: 15000,
                description: "바삭한 후라이드",
                isAvailable: true,
                imageUrl: "image.jpg"
        )

        when:
        def savedMenu = menuService.createMenu(storeId, dto)

        then:
        1 * storeRepository.findById(storeId) >> Optional.of(store)
        1 * menuRepository.save(_) >> { Menu m -> m }

        savedMenu.menuName == "치킨"
        savedMenu.description == "바삭한 후라이드"
    }

    def "메뉴를 수정할 수 있다"() {
        given:
        def menuId = UUID.randomUUID()
        def existingMenu = Menu.builder()
                .id(menuId)
                .menuName("구메뉴")
                .description("기존 설명")
                .build()
        def updateDto = new MenuRequestDto(
                menuName: "신메뉴",
                menuCategoryCode: "new_cat",
                price: 18000,
                description: "새로운 설명",
                isAvailable: false,
                imageUrl: "new.jpg"
        )

        when:
        def updatedMenu = menuService.updateMenu(menuId, updateDto)

        then:
        1 * menuRepository.findById(menuId) >> Optional.of(existingMenu)
        1 * menuRepository.save(_) >> { Menu m -> m }

        updatedMenu.menuName == "신메뉴"
        updatedMenu.description == "새로운 설명"
        !updatedMenu.isAvailable
    }

    def "메뉴를 삭제할 수 있다"() {
        given:
        def menuId = UUID.randomUUID()

        when:
        menuService.deleteMenu(menuId)

        then:
        1 * menuRepository.deleteById(menuId)
    }

    def "메뉴를 조회할 수 있다"() {
        given:
        def menuId = UUID.randomUUID()
        def menu = Menu.builder()
                .id(menuId)
                .menuName("된장찌개")
                .price(8000)
                .description("구수한 찌개")
                .isAvailable(true)
                .build()

        when:
        def result = menuService.getMenu(menuId)

        then:
        1 * menuRepository.findById(menuId) >> Optional.of(menu)

        result.menuName == "된장찌개"
        result.price == 8000
        result.isAvailable
    }

    def "메뉴 목록을 조회할 수 있다"() {
        given:
        def storeId = UUID.randomUUID()
        def store = Store.builder().storeId(storeId).storeName("테스트 가게").build()

        def menu1 = Menu.builder()
                .menuName("김치찌개")
                .menuCategoryCode("korean")
                .price(8000)
                .description("얼큰한 김치찌개")
                .isAvailable(true)
                .store(store)
                .build()

        def menu2 = Menu.builder()
                .menuName("돈까스")
                .menuCategoryCode("japanese")
                .price(10000)
                .description("바삭한 돈까스")
                .isAvailable(true)
                .store(store)
                .build()

        def menuList = [menu1, menu2]

        when:
        def result = menuService.getMenusByStore(storeId)

        then:
        1 * menuRepository.findByStoreStoreId(storeId) >> menuList

        result.size() == 2
        result[0].menuName == "김치찌개"
        result[1].menuName == "돈까스"
    }

}
