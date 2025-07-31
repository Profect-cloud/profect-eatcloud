// Domain/Order/Dto/OrderMenu.java
package profect.eatcloud.Domain.Order.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMenu {
	private UUID menuId;
	private String menuName;
	private Integer quantity;
	private Integer price;  // 단가
	
	// 총 가격 계산 메서드
	public Integer getTotalPrice() {
		return price != null && quantity != null ? price * quantity : 0;
	}
}