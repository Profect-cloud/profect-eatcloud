package profect.eatcloud.Domain.Admin.Dto.response;

import lombok.Data;

@Data
public class AdminOrderResponseDto {
    private String orderId;
    private String orderStatus;
    private String userId;
    // Add more fields as needed
} 