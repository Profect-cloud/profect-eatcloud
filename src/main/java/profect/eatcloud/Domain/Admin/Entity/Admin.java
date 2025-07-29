package profect.eatcloud.Domain.Admin.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import profect.eatcloud.Global.TimeData.TimeData;

@Entity
@Table(name = "p_admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number", length = 18)
    private String phoneNumber;

    @Column(length = 50)
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_time_id", nullable = false)
    private TimeData pTime;
}
