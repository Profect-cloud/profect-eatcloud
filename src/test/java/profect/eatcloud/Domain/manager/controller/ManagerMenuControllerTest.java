package profect.eatcloud.Domain.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import profect.eatcloud.Domain.Manager.Controller.ManagerController;
import profect.eatcloud.Domain.Manager.Service.ManagerService;
import profect.eatcloud.Domain.Store.Controller.MenuController;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Exception.MenuNotFoundException;
import profect.eatcloud.Domain.Store.Service.MenuService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ManagerMenuControllerTest {

    @InjectMocks
    private ManagerController managerController;

    @Mock
    private ManagerService managerService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(managerController).build();
    }

    @Test
    void 메뉴등록_성공() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();
        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuNum(1);
        dto.setMenuName("불고기 덮밥");
        dto.setMenuCategoryCode("KOREAN");
        dto.setPrice(new BigDecimal("8500"));
        dto.setDescription("불고기와 밥 구성");
        dto.setIsAvailable(true);
        dto.setImageUrl("image.jpg");

        Menu savedMenu = Menu.builder()
                .id(UUID.randomUUID())
                .menuName(dto.getMenuName())
                .price(dto.getPrice())
                .build();

        when(managerService.createMenu(eq(storeId), any(MenuRequestDto.class)))
                .thenReturn(savedMenu);

        // when & then
        mockMvc.perform(post("/api/v1/manager/stores/{storeId}/menus", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuName").value("불고기 덮밥"));
    }

    @Test
    void 메뉴등록_실패_메뉴이름없음_400() throws Exception {
        UUID storeId = UUID.randomUUID();

        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuNum(1);
        dto.setPrice(new BigDecimal("8000")); // 이름 누락
        dto.setMenuCategoryCode("KOREAN");

        mockMvc.perform(post("/api/v1/manager/stores/{storeId}/menus", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 메뉴등록_실패_가격음수_400() throws Exception {
        UUID storeId = UUID.randomUUID();

        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuName("음수가격메뉴");
        dto.setMenuNum(2);
        dto.setPrice(new BigDecimal("-1000")); // ❌ 음수 가격
        dto.setMenuCategoryCode("KOREAN");

        mockMvc.perform(post("/api/v1/manager/stores/{storeId}/menus", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void 메뉴수정_성공() throws Exception {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();

        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuNum(1); // ✅ 필수
        dto.setMenuCategoryCode("KOREAN"); // ✅ 필수
        dto.setMenuName("업데이트된 메뉴");
        dto.setPrice(new BigDecimal("13000"));
        dto.setIsAvailable(true);

        Menu updatedMenu = Menu.builder()
                .id(menuId)
                .menuName(dto.getMenuName())
                .price(dto.getPrice())
                .build();

        when(managerService.updateMenu(eq(storeId), eq(menuId), any(MenuRequestDto.class)))
                .thenReturn(updatedMenu); // ✅ 반환값 설정

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/manager/stores/{storeId}/menus/{menuId}", storeId, menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuName").value("업데이트된 메뉴"));
    }


    @Test
    void 메뉴수정_실패_존재하지않는메뉴() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();
        UUID invalidMenuId = UUID.randomUUID();

        MenuRequestDto dto = new MenuRequestDto();
        dto.setMenuName("업데이트된 메뉴");
        dto.setPrice(new BigDecimal("13000"));
        dto.setMenuNum(1); // ✅ 필수 필드 추가
        dto.setMenuCategoryCode("KOREAN"); // ✅ 필수 필드 추가

        doThrow(new MenuNotFoundException("존재하지 않는 메뉴입니다"))
                .when(managerService).updateMenu(eq(storeId), eq(invalidMenuId), any(MenuRequestDto.class));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/manager/stores/{storeId}/menus/{menuId}", storeId, invalidMenuId)
                        .contentType(MediaType.APPLICATION_JSON) // ✅ 여기 contentType
                        .content(objectMapper.writeValueAsString(dto))) // ✅ 여기 content
                .andExpect(status().isNotFound());
    }

    @Test
    void 메뉴삭제_성공() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();

        doNothing().when(managerService).deleteMenu(eq(menuId));

        mockMvc.perform(delete("/api/v1/manager/stores/{storeId}/menus/{menuId}", storeId, menuId))
                .andExpect(status().isNoContent());

    }

    @Test
    void 메뉴삭제_실패_존재하지않는메뉴() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();

        doThrow(new MenuNotFoundException("존재하지 않는 메뉴입니다"))
                .when(managerService).deleteMenu(eq(menuId));

        // when & then
        mockMvc.perform(delete("/api/v1/manager/stores/{storeId}/menus/{menuId}", storeId, menuId))
                .andExpect(status().isNotFound());


    }





}


