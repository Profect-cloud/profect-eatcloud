package profect.eatcloud.Domain.Manager.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Global.TimeData.TimeData;

@Entity
@Table(name = "p_managers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manager {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", length = 18)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_time_id", nullable = false)
    private TimeData pTime;

}

