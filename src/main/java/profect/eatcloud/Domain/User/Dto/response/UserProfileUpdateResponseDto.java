package profect.eatcloud.Domain.User.Dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class UserProfileUpdateResponseDto {
    private String nickname;
    private String phoneNumber;
    private Instant updatedAt;
} 