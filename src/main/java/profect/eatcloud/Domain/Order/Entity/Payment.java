package profect.eatcloud.Domain.Order.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue
    @Column(name = "payment_id", nullable = false)
    private UUID id;

    @Column(name = "payment_key", unique = true, nullable = false, length = 100)
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "method", length = 30)
    private String method;

    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;
} 