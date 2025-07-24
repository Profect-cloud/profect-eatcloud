package profect.eatcloud.Login.Dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String username;
    private String email;
    private String nickname;
    private String roleCode;
} 