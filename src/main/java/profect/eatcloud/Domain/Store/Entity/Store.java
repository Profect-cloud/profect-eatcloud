package profect.eatcloud.Domain.Store.Entity;

import jakarta.persistence.*;
import lombok.*;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;

import java.time.LocalTime;
import java.util.UUID;
import java.sql.Time;

@Entity
@Table(name = "p_stores")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "store_name", nullable = false, length = 200)
    private String storeName;

    @Column(name = "store_address", length = 300)
    private String storeAddress;

    @Column(name = "phone_number", length = 18)
    private String phoneNumber;

    @Column(name = "min_cost", nullable = false)
    private Integer minCost = 0;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "store_lat")
    private Double storeLat;

    @Column(name = "store_lon")
    private Double storeLon;

    @Column(name = "open_status")
    private Boolean openStatus;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}