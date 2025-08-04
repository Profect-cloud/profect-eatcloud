package profect.eatcloud.domain.store.dto;

import lombok.Builder;
import profect.eatcloud.domain.store.entity.Menu;

import java.math.BigDecimal;
import java.util.UUID;


@Builder
// MenuResponseDto 만들기
public record MenuResponseDto(
        UUID id,
        int menuNum,
        String menuName,
        String menuCategoryCode,
        BigDecimal price,
        String description,
        Boolean isAvailable,
        String imageUrl
) {
    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(
                menu.getId(),
                menu.getMenuNum(),
                menu.getMenuName(),
                menu.getMenuCategoryCode(),
                menu.getPrice(),
                menu.getDescription(),
                menu.getIsAvailable(),
                menu.getImageUrl()
        );
    }
}
