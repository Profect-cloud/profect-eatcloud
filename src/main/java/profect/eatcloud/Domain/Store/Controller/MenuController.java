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
@RequestMapping("/api/v1/stores/{store_id}/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<Menu>> getMenus(@PathVariable UUID store_id) {
        return ResponseEntity.ok(menuService.getMenusByStore(store_id));
    }

    @GetMapping("/{menu_id}")
    public ResponseEntity<Menu> getMenuDetail(@PathVariable UUID store_id, @PathVariable UUID menu_id) {
        return ResponseEntity.ok(menuService.getMenuById(store_id,menu_id));
    }

}

