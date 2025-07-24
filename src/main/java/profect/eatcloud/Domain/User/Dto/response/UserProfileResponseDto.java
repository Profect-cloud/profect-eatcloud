package profect.eatcloud.Domain.User.Dto.response;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {
    private String username;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;
} 