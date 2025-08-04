package profect.eatcloud.domain.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import profect.eatcloud.domain.store.dto.StoreSearchByMenuCategoryRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchResponseDto;
import profect.eatcloud.domain.store.service.StoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
@AllArgsConstructor

@Tag(name = "6. StoreController")
public class StoreController {

    private final StoreService storeService;


    @Operation(summary = "매장 카테고리 별 거리기반 매장 조회")
    @GetMapping("/search/category")
    public ResponseEntity<List<StoreSearchResponseDto>> searchStoresByCategoryAndDistance(
            @RequestParam UUID categoryId,
            @RequestParam double userLat,
            @RequestParam double userLon,
            @RequestParam(defaultValue = "3") double distanceKm // 기본 거리: 3km
    ) {
        List<StoreSearchResponseDto> stores = storeService.searchStoresByCategoryAndDistance(categoryId, userLat, userLon, distanceKm);
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "메뉴 카테고리 별 거리 기반 매장 검색")
    @GetMapping("/search/menu-category-distance")
    public ResponseEntity<List<StoreSearchResponseDto>> searchStoresByMenuCategoryAndDistance(
            @RequestParam String categoryCode,
            @RequestParam double userLat,
            @RequestParam double userLon,
            @RequestParam(defaultValue = "3.0") double distanceKm
    ) {
        StoreSearchByMenuCategoryRequestDto request = new StoreSearchByMenuCategoryRequestDto(
                categoryCode, userLat, userLon, distanceKm
        );
        return ResponseEntity.ok(storeService.searchStoresByMenuCategory(request));
    }

}