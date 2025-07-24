package profect.eatcloud.Domain.User.Dto.response;

import lombok.Data;

@Data
public class CartItemResponseDto {
    private String storeId;
    private Integer menuNum;
    private Integer quantity;
    // Add more fields as needed
} 