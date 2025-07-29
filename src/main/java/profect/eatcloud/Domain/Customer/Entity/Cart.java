package profect.eatcloud.Domain.Customer.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import profect.eatcloud.Domain.Customer.Dto.CartItem;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "p_cart")
@Getter @Setter
public class Cart extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cart_id")
	private UUID cartId;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "cart_items", nullable = false, columnDefinition = "jsonb")
	private List<CartItem> cartItems;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;
}