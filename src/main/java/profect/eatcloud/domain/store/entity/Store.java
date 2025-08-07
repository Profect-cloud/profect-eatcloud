package profect.eatcloud.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;
import profect.eatcloud.domain.globalCategory.entity.StoreCategory;
import profect.eatcloud.domain.store.dto.StoreRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchResponseDto;
import profect.eatcloud.global.timeData.BaseTimeEntity;

import java.awt.*;
import java.time.LocalTime;
import java.util.UUID;


@Entity
@Table(name = "p_stores")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
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
    @Builder.Default
    private Integer minCost = 0;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "store_lat")
    private Double storeLat;

    @Column(name = "store_lon")
    private Double storeLon;

    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point location;

    @Column(name = "open_status")
    private Boolean openStatus;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private StoreCategory storeCategory;

}