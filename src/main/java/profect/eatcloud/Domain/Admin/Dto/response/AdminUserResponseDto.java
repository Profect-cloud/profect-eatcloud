package profect.eatcloud.Domain.Admin.Dto.response;

import lombok.Data;

@Data
public class AdminUserResponseDto {
    private String userId;
    private String username;
    private String email;
    private String role;
    // Add more fields as needed
} 