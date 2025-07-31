package profect.eatcloud.Login.dto;

import lombok.Getter;

@Getter
public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
}
