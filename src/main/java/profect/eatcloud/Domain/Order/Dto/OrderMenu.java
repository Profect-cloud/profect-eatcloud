// Domain/Order/Dto/OrderMenu.java
package profect.eatcloud.Domain.Order.Dto;

import lombok.Data;
import java.util.UUID;

@Data
public class OrderMenu {
	private UUID menuId;
	private String menuName;
	private Integer quantity;
	private Integer unitPrice;
	private Integer totalPrice;
}