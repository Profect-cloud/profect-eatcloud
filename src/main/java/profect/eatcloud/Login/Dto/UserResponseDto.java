package profect.eatcloud.Login.Dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class UserResponseDto {
    private UUID id;
    private String username;
    private String nickname;
    private String email;
    private String roleCode;
    private String phoneNumber;
    private UUID pTimeId;
} 