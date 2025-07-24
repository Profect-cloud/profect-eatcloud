package profect.eatcloud.Domain.User.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "order_number", nullable = false, length = 50, unique = true)
    private String orderNumber;

    @Column(name = "user_id", nullable = false, length = 10)
    private String userId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "order_status", nullable = false, length = 30)
    private String orderStatus;

    @Column(name = "order_type", nullable = false, length = 30)
    private String orderType;

    @Column(name = "order_menu_list", nullable = false, columnDefinition = "jsonb")
    private String orderMenuList;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;
} 