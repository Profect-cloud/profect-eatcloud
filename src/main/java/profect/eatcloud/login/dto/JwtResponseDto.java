package profect.eatcloud.login.dto;

import lombok.Getter;

@Getter
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    public JwtResponseDto(String token) {
        this.token = token;
    }
}
