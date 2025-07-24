package profect.eatcloud.Domain.User.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "user_id", nullable = false, length = 10)
    private String userId;

    @Column(name = "cart_items", nullable = false, columnDefinition = "jsonb")
    private String cartItems;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;
} 