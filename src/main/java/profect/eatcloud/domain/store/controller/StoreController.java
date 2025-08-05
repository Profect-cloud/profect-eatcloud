package profect.eatcloud.domain.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.domain.store.dto.StoreSearchByMenuCategoryRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchResponseDto;
import profect.eatcloud.domain.store.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@AllArgsConstructor

@Tag(name = "6. StoreController")
public class StoreController {

    private final StoreService storeService;


    @Operation(summary = "1. 매장 카테고리 별 거리기반 매장 조회")
    @GetMapping("/search/category")
    public ResponseEntity<List<StoreSearchResponseDto>> searchStoresByCategoryAndDistance(
            @ModelAttribute StoreSearchRequestDto condition
    ) {
        List<StoreSearchResponseDto> stores = storeService.searchStoresByCategoryAndDistance(condition);
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "2. 메뉴 카테고리 별 거리 기반 매장 검색")
    @GetMapping("/search/menu-category")
    public ResponseEntity<List<StoreSearchResponseDto>> searchStoresByMenuCategoryAndDistance(
            @ModelAttribute StoreSearchByMenuCategoryRequestDto condition
    ) {
        List<StoreSearchResponseDto> stores = storeService.searchStoresByMenuCategory(condition);
        return ResponseEntity.ok(stores);
    }

}