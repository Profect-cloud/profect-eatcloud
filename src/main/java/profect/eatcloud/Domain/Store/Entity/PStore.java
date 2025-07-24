package profect.eatcloud.Domain.Store.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalTime;

@Entity
@Table(name = "p_stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PStore {
    @Id
    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "store_name", nullable = false, length = 200)
    private String storeName;

    @Column(name = "store_address", length = 500)
    private String storeAddress;

    @Column(name = "phone_number", length = 18)
    private String phoneNumber;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "store_lat")
    private Double storeLat;

    @Column(name = "store_lon")
    private Double storeLon;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;
} 