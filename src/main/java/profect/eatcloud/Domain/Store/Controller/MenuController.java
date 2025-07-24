package profect.eatcloud.Domain.Store.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.Domain.Store.Entity.Menu;
import profect.eatcloud.Domain.Store.Service.MenuService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stores/{store_id}/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<Menu> createMenu(@PathVariable UUID store_id, @RequestBody MenuRequestDto dto) {
        return ResponseEntity.ok(menuService.createMenu(store_id, dto));
    }

    @GetMapping
    public ResponseEntity<List<Menu>> getMenus(@PathVariable UUID store_id) {
        return ResponseEntity.ok(menuService.getMenusByStore(store_id));
    }

    @GetMapping("/{menu_id}")
    public ResponseEntity<Menu> getMenuDetail(@PathVariable UUID store_id, @PathVariable UUID menu_id) {
        // 실제 구현 시 menuService.getMenuDetail(store_id, menu_id) 등으로 대체
        return ResponseEntity.ok(new Menu()); // 임시 반환
    }

    @PostMapping("/{menu_id}/ai-description")
    public ResponseEntity<String> requestAIDescription(@PathVariable UUID store_id, @PathVariable UUID menu_id) {
        // 실제 구현 시 menuService.requestAIDescription(store_id, menu_id) 등으로 대체
        return ResponseEntity.ok("AI 상품설명 요청 (미구현)");
    }

    @PutMapping("/{menu_id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable UUID menu_id, @RequestBody MenuRequestDto dto) {
        return ResponseEntity.ok(menuService.updateMenu(menu_id, dto));
    }

    @DeleteMapping("/{menu_id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID menu_id) {
        menuService.deleteMenu(menu_id);
        return ResponseEntity.noContent().build();
    }
}
