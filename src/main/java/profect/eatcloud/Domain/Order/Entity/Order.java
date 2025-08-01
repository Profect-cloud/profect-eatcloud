package profect.eatcloud.Domain.Order.Entity;

import jakarta.persistence.*;
import lombok.*;
import profect.eatcloud.Domain.Order.Dto.OrderMenu;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Payment.Entity.Payment;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderStatusCode;
import profect.eatcloud.Domain.GlobalCategory.Entity.OrderTypeCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "p_orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "order_id")
	private UUID orderId;

	@Column(name = "order_number", nullable = false, length = 50, unique = true)
	private String orderNumber;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "order_menu_list", nullable = false, columnDefinition = "jsonb")
	private List<OrderMenu> orderMenuList;

	@Column(name = "customer_id", nullable = false)
	private UUID customerId;

	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Column(name = "payment_id")
	private UUID paymentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_status", referencedColumnName = "code")
	private OrderStatusCode orderStatusCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_type", referencedColumnName = "code")
	private OrderTypeCode orderTypeCode;

	//BaseTimeEntity->TimeData->GetCreatedAt



}