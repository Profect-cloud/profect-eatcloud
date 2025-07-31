package profect.eatcloud.Domain.Customer.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateCartItemRequest {
    private UUID menuId;
    private Integer quantity;
}
