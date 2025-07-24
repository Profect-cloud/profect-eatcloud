package profect.eatcloud.Domain.User.Dto.request;

import lombok.Data;

@Data
public class CartItemRequestDto {
    private String storeId;
    private Integer menuNum;
    private Integer quantity;
} 