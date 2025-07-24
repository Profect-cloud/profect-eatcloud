package profect.eatcloud.Domain.User.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "userid", nullable = false, length = 10)
    private String userid;

    @Column(name = "zipcode", length = 10)
    private String zipcode;

    @Column(name = "road_addr", length = 500)
    private String roadAddr;

    @Column(name = "detail_addr", length = 200)
    private String detailAddr;

    @Column(name = "is_selected")
    private Boolean isSelected;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;
} 