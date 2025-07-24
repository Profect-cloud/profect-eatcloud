package profect.eatcloud.Login.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAuthRequestDto {
    private String email;
    private String code;
} 