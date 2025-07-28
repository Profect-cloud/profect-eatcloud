package profect.eatcloud.Domain.Store.Dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class MenuResponseDto {
    private UUID menuId;
    private int menuNum;
    private String menuName;
    private String menuCategoryCode;
    private BigDecimal price;
    private String description;
    private Boolean isAvailable;
    private String imageUrl;
    private UUID pTimeId;
}
