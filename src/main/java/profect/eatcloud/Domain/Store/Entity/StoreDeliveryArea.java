package profect.eatcloud.Domain.Store.Entity;

import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.UUID;

@Entity
@Table(name = "p_store_delivery_areas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(StoreDeliveryAreaId.class)
public class StoreDeliveryArea extends BaseTimeEntity {

    @Id
    @Column(name = "store_id")
    private UUID storeId;

    @Id
    @Column(name = "area_id")
    private UUID areaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", insertable = false, updatable = false)
    private DeliveryArea deliveryArea;

    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee = 0;
}