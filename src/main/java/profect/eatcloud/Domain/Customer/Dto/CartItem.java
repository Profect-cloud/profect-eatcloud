package profect.eatcloud.Domain.Customer.Dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CartItem {
	private UUID menuId;
	private String menuName;
	private Integer quantity;
	private Integer price;
}