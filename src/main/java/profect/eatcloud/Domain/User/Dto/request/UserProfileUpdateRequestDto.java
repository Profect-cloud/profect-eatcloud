package profect.eatcloud.Domain.User.Dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequestDto {
    private String nickname;
    private String phoneNumber;
} 