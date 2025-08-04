package profect.eatcloud.domain.Manager.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import profect.eatcloud.domain.Store.Entity.Store;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;

@Entity
@Table(name = "p_managers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manager extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", length = 18)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
}

