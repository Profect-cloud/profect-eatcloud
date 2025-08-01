package profect.eatcloud.Domain.Store.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import profect.eatcloud.Domain.Store.Dto.StoreResponseDto;
import profect.eatcloud.Domain.Store.Dto.StoreSearchResponseDto;
import profect.eatcloud.Domain.Store.Service.StoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Tag(name = "6. StoreController")
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "메뉴 카테고리로 매장 검색")
    @GetMapping("/search/menu-category")
    public ResponseEntity<List<StoreResponseDto>> getStoresByMenuCategory(@RequestParam String code) {
        return ResponseEntity.ok(storeService.searchStoresByMenuCategory(code));
    }

    @Operation(summary = "매장 카테고리별 거리기반 매장 조회")
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

}