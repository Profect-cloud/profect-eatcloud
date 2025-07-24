package profect.eatcloud.Domain.User.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 18)
    private String username;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "role_code", nullable = false, length = 30)
    private String roleCode;

    @Column(name = "phone_number", length = 18)
    private String phoneNumber;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;
} 