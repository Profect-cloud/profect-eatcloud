package profect.eatcloud.Login.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String password;
    private String role; // "CUSTOMER", "MANAGER", "ADMIN"
}
