package profect.eatcloud.domain.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import profect.eatcloud.domain.store.dto.StoreSearchByMenuCategoryRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchResponseDto;
import profect.eatcloud.domain.store.repository.StoreRepository_min;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("매장 서비스 테스트")
class StoreServiceTest {

    private StoreService storeService;
    private StoreRepository_min storeRepository;

    @BeforeEach
    void setUp() {
        storeRepository = mock(StoreRepository_min.class);
        storeService = new StoreService(storeRepository);
    }

    @Test
    @DisplayName("카테고리별 거리기반 매장 조회 - 성공")
    void searchStoresByCategoryAndDistance_Success() {
        // given
        UUID categoryId = UUID.randomUUID();
        double userLat = 37.5665;
        double userLon = 126.9780;
        double distanceKm = 5.0;

        StoreSearchRequestDto requestDto = new StoreSearchRequestDto();
        requestDto.setCategoryId(categoryId);
        requestDto.setUserLat(userLat);
        requestDto.setUserLon(userLon);
        requestDto.setDistanceKm(distanceKm);

        List<StoreSearchResponseDto> expectedStores = Arrays.asList(
                StoreSearchResponseDto.of(
                        UUID.randomUUID(),
                        "맛있는 치킨집",
                        "서울시 강남구 테헤란로 123",
                        37.5665,
                        126.9780,
                        15000,
                        true
                ),
                StoreSearchResponseDto.of(
                        UUID.randomUUID(),
                        "신선한 피자집",
                        "서울시 강남구 역삼동 456",
                        37.5670,
                        126.9785,
                        20000,
                        true
                )
        );

        when(storeRepository.findStoresByCategoryWithinDistance(
                eq(categoryId), eq(userLat), eq(userLon), eq(distanceKm)
        )).thenReturn(expectedStores);

        // when
        List<StoreSearchResponseDto> result = storeService.searchStoresByCategoryAndDistance(requestDto);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("맛있는 치킨집", result.get(0).getStoreName());
        assertEquals("신선한 피자집", result.get(1).getStoreName());
        assertTrue(result.get(0).getOpenStatus());
        assertTrue(result.get(1).getOpenStatus());

        verify(storeRepository, times(1)).findStoresByCategoryWithinDistance(
                eq(categoryId), eq(userLat), eq(userLon), eq(distanceKm)
        );
    }

    @Test
    @DisplayName("카테고리별 거리기반 매장 조회 - 결과 없음")
    void searchStoresByCategoryAndDistance_EmptyResult() {
        // given
        UUID categoryId = UUID.randomUUID();
        double userLat = 37.5665;
        double userLon = 126.9780;
        double distanceKm = 1.0; // 짧은 거리

        StoreSearchRequestDto requestDto = new StoreSearchRequestDto();
        requestDto.setCategoryId(categoryId);
        requestDto.setUserLat(userLat);
        requestDto.setUserLon(userLon);
        requestDto.setDistanceKm(distanceKm);

        when(storeRepository.findStoresByCategoryWithinDistance(
                eq(categoryId), eq(userLat), eq(userLon), eq(distanceKm)
        )).thenReturn(Collections.emptyList());

        // when
        List<StoreSearchResponseDto> result = storeService.searchStoresByCategoryAndDistance(requestDto);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(storeRepository, times(1)).findStoresByCategoryWithinDistance(
                eq(categoryId), eq(userLat), eq(userLon), eq(distanceKm)
        );
    }

    @Test
    @DisplayName("카테고리별 거리기반 매장 조회 - 기본 거리 사용")
    void searchStoresByCategoryAndDistance_DefaultDistance() {
        // given
        UUID categoryId = UUID.randomUUID();
        double userLat = 37.5665;
        double userLon = 126.9780;

        StoreSearchRequestDto requestDto = new StoreSearchRequestDto();
        requestDto.setCategoryId(categoryId);
        requestDto.setUserLat(userLat);
        requestDto.setUserLon(userLon);
        // distanceKm는 기본값 3.0 사용

        List<StoreSearchResponseDto> expectedStores = Arrays.asList(
                StoreSearchResponseDto.of(
                        UUID.randomUUID(),
                        "가까운 매장",
                        "서울시 강남구 가까운곳",
                        37.5665,
                        126.9780,
                        10000,
                        true
                )
        );

        when(storeRepository.findStoresByCategoryWithinDistance(
                eq(categoryId), eq(userLat), eq(userLon), eq(3.0)
        )).thenReturn(expectedStores);

        // when
        List<StoreSearchResponseDto> result = storeService.searchStoresByCategoryAndDistance(requestDto);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("가까운 매장", result.get(0).getStoreName());

        verify(storeRepository, times(1)).findStoresByCategoryWithinDistance(
                eq(categoryId), eq(userLat), eq(userLon), eq(3.0)
        );
    }

    @Test
    @DisplayName("메뉴 카테고리별 거리기반 매장 조회 - 성공")
    void searchStoresByMenuCategory_Success() {
        // given
        String menuCategoryCode = "CHICKEN";
        double userLat = 37.5665;
        double userLon = 126.9780;
        double distanceKm = 5.0;

        StoreSearchByMenuCategoryRequestDto requestDto = new StoreSearchByMenuCategoryRequestDto();
        requestDto.setCategoryCode(menuCategoryCode);
        requestDto.setUserLat(userLat);
        requestDto.setUserLon(userLon);
        requestDto.setDistanceKm(distanceKm);

        List<StoreSearchResponseDto> expectedStores = Arrays.asList(
                StoreSearchResponseDto.of(
                        UUID.randomUUID(),
                        "치킨 전문점",
                        "서울시 강남구 치킨거리 123",
                        37.5665,
                        126.9780,
                        18000,
                        true
                )
        );

        when(storeRepository.findStoresByMenuCategoryWithinDistance(
                eq(menuCategoryCode), eq(userLat), eq(userLon), eq(distanceKm)
        )).thenReturn(expectedStores);

        // when
        List<StoreSearchResponseDto> result = storeService.searchStoresByMenuCategory(requestDto);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("치킨 전문점", result.get(0).getStoreName());
        assertEquals("CHICKEN", menuCategoryCode);

        verify(storeRepository, times(1)).findStoresByMenuCategoryWithinDistance(
                eq(menuCategoryCode), eq(userLat), eq(userLon), eq(distanceKm)
        );
    }

    @Test
    @DisplayName("매장 정보 검증 - 필수 필드 확인")
    void validateStoreResponseFields() {
        // given
        UUID categoryId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        String storeName = "테스트 매장";
        String storeAddress = "서울시 강남구 테스트로 123";
        Double storeLat = 37.5665;
        Double storeLon = 126.9780;
        Integer minCost = 15000;
        Boolean openStatus = true;

        StoreSearchRequestDto requestDto = new StoreSearchRequestDto();
        requestDto.setCategoryId(categoryId);
        requestDto.setUserLat(37.5665);
        requestDto.setUserLon(126.9780);
        requestDto.setDistanceKm(3.0);

        List<StoreSearchResponseDto> expectedStores = Arrays.asList(
                StoreSearchResponseDto.of(storeId, storeName, storeAddress, storeLat, storeLon, minCost, openStatus)
        );

        when(storeRepository.findStoresByCategoryWithinDistance(any(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(expectedStores);

        // when
        List<StoreSearchResponseDto> result = storeService.searchStoresByCategoryAndDistance(requestDto);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        StoreSearchResponseDto store = result.get(0);
        assertEquals(storeId, store.getStoreId());
        assertEquals(storeName, store.getStoreName());
        assertEquals(storeAddress, store.getStoreAddress());
        assertEquals(storeLat, store.getStoreLat());
        assertEquals(storeLon, store.getStoreLon());
        assertEquals(minCost, store.getMinCost());
        assertEquals(openStatus, store.getOpenStatus());
    }

    @Test
    @DisplayName("매장 정보 검증 - null 값 처리")
    void validateStoreResponseWithNullValues() {
        // given
        UUID categoryId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        StoreSearchRequestDto requestDto = new StoreSearchRequestDto();
        requestDto.setCategoryId(categoryId);
        requestDto.setUserLat(37.5665);
        requestDto.setUserLon(126.9780);
        requestDto.setDistanceKm(3.0);

        // 일부 필드가 null인 경우
        List<StoreSearchResponseDto> expectedStores = Arrays.asList(
                StoreSearchResponseDto.of(
                        storeId,
                        "테스트 매장",
                        null, // 주소가 null
                        37.5665,
                        126.9780,
                        null, // 최소 주문 금액이 null
                        false // 영업 종료
                )
        );

        when(storeRepository.findStoresByCategoryWithinDistance(any(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(expectedStores);

        // when
        List<StoreSearchResponseDto> result = storeService.searchStoresByCategoryAndDistance(requestDto);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        StoreSearchResponseDto store = result.get(0);
        assertEquals(storeId, store.getStoreId());
        assertEquals("테스트 매장", store.getStoreName());
        assertNull(store.getStoreAddress());
        assertEquals(37.5665, store.getStoreLat());
        assertEquals(126.9780, store.getStoreLon());
        assertNull(store.getMinCost());
        assertFalse(store.getOpenStatus());
    }
} 