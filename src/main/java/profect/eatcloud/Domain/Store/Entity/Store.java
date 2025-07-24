package profect.eatcloud.Domain.Store.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "p_stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue
    @Column(name = "store_id", nullable = false)
    private UUID id;

    @Column(name = "store_name", nullable = false, length = 200)
    private String storeName;

    @Column(name = "store_address", length = 500)
    private String storeAddress;

    @Column(name = "phone_number", length = 18)
    private String phoneNumber;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "min_cost", nullable = false)
    private int minCost = 0;

    @Column(name = "discription", columnDefinition = "TEXT")
    private String description;

    @Column(name = "store_lat")
    private Double storeLat;

    @Column(name = "store_lon")
    private Double storeLon;

    @Column(name = "open_status")
    private Boolean openStatus;

    @Column(name = "open_time", nullable = false)
    private java.time.LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private java.time.LocalTime closeTime;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();
}
