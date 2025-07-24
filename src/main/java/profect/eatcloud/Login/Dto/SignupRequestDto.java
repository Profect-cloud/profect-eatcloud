package profect.eatcloud.Login.Dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class SignupRequestDto {
    private String username;
    private String nickname;
    private String email;
    private String password;
    private String roleCode;
    private String phoneNumber;
    private UUID pTimeId;
} 