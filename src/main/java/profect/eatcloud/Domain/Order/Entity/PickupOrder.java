package profect.eatcloud.Domain.Order.Entity;

import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_pickup_orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupOrder extends BaseTimeEntity {

    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "pickup_requests", columnDefinition = "TEXT")
    private String pickupRequests;

    @Column(name = "estimated_pickup_time")
    private LocalDateTime estimatedPickupTime;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "canceled_by", length = 100)
    private String canceledBy;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;
}