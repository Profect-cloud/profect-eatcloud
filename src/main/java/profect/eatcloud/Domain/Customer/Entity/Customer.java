package profect.eatcloud.Domain.Customer.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import profect.eatcloud.Global.TimeData.TimeData;

@Entity
@Table(name = "p_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(length = 100)
    private String nickname;

    @Column(length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", length = 18)
    private String phoneNumber;

    private Integer points;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_time_id", nullable = false)
    private TimeData pTime;

}
