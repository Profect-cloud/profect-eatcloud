package profect.eatcloud.domain.Store.Entity;

import jakarta.persistence.*;
import lombok.*;
import profect.eatcloud.domain.Store.Dto.StoreSearchResponseDto;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "p_stores")
@SqlResultSetMapping(
        name = "StoreSearchResponseMapping",
        classes = @ConstructorResult(
                targetClass = StoreSearchResponseDto.class,
                columns = {
                        @ColumnResult(name = "store_id", type = UUID.class),
                        @ColumnResult(name = "store_name", type = String.class),
                        @ColumnResult(name = "store_address", type = String.class),
                        @ColumnResult(name = "store_lat", type = Double.class),
                        @ColumnResult(name = "store_lon", type = Double.class),
                        @ColumnResult(name = "min_cost", type = Integer.class),
                        @ColumnResult(name = "open_status", type = Boolean.class)
                }
        )
)
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
    @Builder.Default
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