package profect.eatcloud.domain.Store.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import profect.eatcloud.domain.Store.Entity.Menu;
import profect.eatcloud.domain.Store.Service.MenuService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{store_id}/menus")
@RequiredArgsConstructor
@Tag(name = "7. MenuController")
public class MenuController {
    private final MenuService menuService;

    @Operation(summary = "단일 매장 메뉴 리스트 조회")
    @GetMapping
    public ResponseEntity<List<Menu>> getMenus(@PathVariable UUID store_id) {
        return ResponseEntity.ok(menuService.getMenusByStore(store_id));
    }

    @Operation(summary = "메뉴 상세 조회")
    @GetMapping("/{menu_id}")
    public ResponseEntity<Menu> getMenuDetail(@PathVariable UUID store_id, @PathVariable UUID menu_id) {
        return ResponseEntity.ok(menuService.getMenuById(store_id,menu_id));
    }

}

