package profect.eatcloud.Domain.Customer.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import profect.eatcloud.Global.TimeData.TimeData;
import profect.eatcloud.Security.userDetails.CustomUserDetails;

@Entity
@Table(name = "p_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer implements CustomUserDetails {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }
}
