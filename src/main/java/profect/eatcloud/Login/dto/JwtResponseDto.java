package profect.eatcloud.Login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    public JwtResponseDto(String token) {
        this.token = token;
    }
}
