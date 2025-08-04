package profect.eatcloud.domain.customer.Dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CartItem {
	private UUID menuId;
	private String menuName;
	private Integer quantity;
	private Integer price;
	private UUID storeId;
}