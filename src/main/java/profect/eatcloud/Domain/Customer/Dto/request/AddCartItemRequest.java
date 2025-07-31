package profect.eatcloud.Domain.Customer.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AddCartItemRequest {
    private UUID menuId;
    private String menuName;
    private Integer price;
    private Integer quantity;
    private UUID storeId;
}
