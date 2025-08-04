package profect.eatcloud.domain.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.domain.manager.service.ManagerService;
import profect.eatcloud.domain.Store.Dto.AiDescriptionRequestDto;
import profect.eatcloud.domain.Store.Dto.AiDescriptionResponseDto;
import profect.eatcloud.domain.Store.Dto.MenuRequestDto;
import profect.eatcloud.domain.Store.Dto.MenuResponseDto;
import profect.eatcloud.domain.Store.Entity.Menu;
import profect.eatcloud.domain.Store.Service.AiDescriptionService;

import java.util.UUID;
@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
@Tag(name = "5. ManagerController")
public class ManagerController {

    private final ManagerService managerService;
    private final AiDescriptionService aiDescriptionService;

    // 메뉴 관리
    @Operation(summary = "1. 메뉴 생성")
    @PostMapping("/stores/{store_id}/menus")
    public ResponseEntity<MenuResponseDto> createMenu(@PathVariable UUID store_id, @RequestBody @Valid MenuRequestDto dto) {
        Menu created = managerService.createMenu(store_id, dto);
        return ResponseEntity.ok(MenuResponseDto.from(created));
    }

    @Operation(summary = "2. 메뉴 수정")
    @PutMapping("/stores/{store_id}/menus/{menu_id}")
    public ResponseEntity<MenuResponseDto> updateMenu(@PathVariable UUID store_id,
                                                      @PathVariable UUID menu_id,
                                                      @RequestBody @Valid MenuRequestDto dto) {
        Menu updated = managerService.updateMenu(store_id, menu_id, dto);
        return ResponseEntity.ok(MenuResponseDto.from(updated));
    }

    @Operation(summary = "3. 메뉴 삭제")
    @DeleteMapping("/stores/{store_id}/menus/{menu_id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID menu_id) {
        managerService.deleteMenu(menu_id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "4. AI 메뉴 설명 생성")
    @PostMapping("/stores/{store_id}/menus/ai-description")
    public ResponseEntity<AiDescriptionResponseDto> generateAIDescription(
            @PathVariable UUID store_id,
            @RequestBody @Valid AiDescriptionRequestDto requestDto) {

        String description = aiDescriptionService.generateDescription(requestDto);
        return ResponseEntity.ok(new AiDescriptionResponseDto(description));
    }
}
