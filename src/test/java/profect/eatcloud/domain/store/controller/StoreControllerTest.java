package profect.eatcloud.domain.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import profect.eatcloud.domain.store.dto.StoreSearchByMenuCategoryRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchResponseDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("매장 컨트롤러 통합 테스트")
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID categoryId;
    private UUID storeId1;
    private UUID storeId2;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        storeId1 = UUID.randomUUID();
        storeId2 = UUID.randomUUID();
    }

    @Test
    @DisplayName("카테고리별 거리기반 매장 조회 API - 필수 파라미터 누락")
    void searchStoresByCategoryAndDistance_MissingRequiredParameters() throws Exception {
        double userLat = 37.5665;
        double userLon = 126.9780;

        mockMvc.perform(get("/api/v1/stores/search/category")
                        .param("userLat", String.valueOf(userLat))
                        .param("userLon", String.valueOf(userLon))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리별 거리기반 매장 조회 API - 잘못된 좌표값")
    void searchStoresByCategoryAndDistance_InvalidCoordinates() throws Exception {
        double invalidLat = 91.0;
        double userLon = 126.9780;

        mockMvc.perform(get("/api/v1/stores/search/category")
                        .param("categoryId", categoryId.toString())
                        .param("userLat", String.valueOf(invalidLat))
                        .param("userLon", String.valueOf(userLon))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리별 거리기반 매장 조회 API - 음수 거리값")
    void searchStoresByCategoryAndDistance_NegativeDistance() throws Exception {
        double userLat = 37.5665;
        double userLon = 126.9780;
        double negativeDistance = -1.0;

        mockMvc.perform(get("/api/v1/stores/search/category")
                        .param("categoryId", categoryId.toString())
                        .param("userLat", String.valueOf(userLat))
                        .param("userLon", String.valueOf(userLon))
                        .param("distanceKm", String.valueOf(negativeDistance))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
