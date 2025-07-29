package profect.eatcloud.Domain.Store.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Store.Dto.AiDescriptionRequestDto;
import profect.eatcloud.Domain.Store.Dto.AiDescriptionResponseDto;
import profect.eatcloud.Domain.Store.Service.AiDescriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores/{storeId}/menus")
public class AiDescriptionController {

    private final AiDescriptionService aiDescriptionService;

    @PostMapping("/ai-description")
    public ResponseEntity<AiDescriptionResponseDto> generateDescription(
            @PathVariable Long storeId,
            @RequestBody AiDescriptionRequestDto requestDto) {

        String description = aiDescriptionService.generateDescription(requestDto);
        return ResponseEntity.ok(new AiDescriptionResponseDto(description));
    }

}

