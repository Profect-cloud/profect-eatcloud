package profect.eatcloud.Domain.store.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import profect.eatcloud.Domain.Store.Controller.MenuController;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Service.MenuService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import profect.eatcloud.Security.jwt.JwtTokenProvider;

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MenuControllerTest.Config.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuService menuService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class Config {
        @Bean
        public MenuService menuService() {
            return Mockito.mock(MenuService.class);
        }
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

        when(menuService.createMenu(eq(storeId), any(MenuRequestDto.class)))
                .thenReturn(savedMenu);

        // when & then
        mockMvc.perform(post("/api/v1/stores/{storeId}/menus", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuName").value("불고기 덮밥"));
    }
}

