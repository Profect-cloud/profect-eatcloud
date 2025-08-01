package profect.eatcloud.Domain.Manager.Controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Manager.Service.ManagerService;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.Domain.Store.Dto.MenuResponseDto;
import profect.eatcloud.Domain.Store.Entity.Menu;

import java.util.UUID;
@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;


    // 메뉴 관리
    @Operation(summary = "1. 메뉴 생성")
    @PostMapping("/stores/{store_id}/menus")
    public ResponseEntity<MenuResponseDto> createMenu(@PathVariable UUID store_id, @RequestBody @Valid MenuRequestDto dto) {
        Menu created = managerService.createMenu(store_id, dto);
        return ResponseEntity.ok(MenuResponseDto.from(created));
    }
    @Operation(summary = "2. AI 메뉴 설명 생성")
    @PostMapping("/stores/{store_id}/menus/{menu_id}/ai-description")
    public ResponseEntity<String> requestAIDescription(@PathVariable UUID store_id, @PathVariable UUID menu_id) {
        return ResponseEntity.ok("AI 상품설명 요청 (미구현)");
    }

    @Operation(summary = "3. 메뉴 수정")
    @PutMapping("/stores/{store_id}/menus/{menu_id}")
    public ResponseEntity<MenuResponseDto> updateMenu(@PathVariable UUID store_id,
                                                      @PathVariable UUID menu_id,
                                                      @RequestBody @Valid MenuRequestDto dto) {
        Menu updated = managerService.updateMenu(store_id, menu_id, dto);
        return ResponseEntity.ok(MenuResponseDto.from(updated));
    }

    @Operation(summary = "4. 메뉴 삭제")
    @DeleteMapping("/stores/{store_id}/menus/{menu_id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID menu_id) {
        managerService.deleteMenu(menu_id);
        return ResponseEntity.noContent().build();
    }
}
