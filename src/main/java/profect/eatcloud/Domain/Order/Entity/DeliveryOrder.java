package profect.eatcloud.Domain.Order.Entity;

import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_delivery_orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryOrder extends BaseTimeEntity {

    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "delivery_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "delivery_requests", columnDefinition = "TEXT")
    private String deliveryRequests;

    @Column(name = "zipcode", length = 10)
    private String zipcode;

    @Column(name = "road_addr", length = 500)
    private String roadAddr;

    @Column(name = "detail_addr", length = 200)
    private String detailAddr;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "estimated_preparation_time")
    private Integer estimatedPreparationTime;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "canceled_by", length = 100)
    private String canceledBy;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;
}