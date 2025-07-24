package profect.eatcloud.Domain.Store.Dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class MenuRequestDto {
    private int menuNum;
    private String menuName;
    private String menuCategoryCode;
    private BigDecimal price;
    private String description;
    private Boolean isAvailable;
    private String imageUrl;
    private UUID pTimeId;
}
