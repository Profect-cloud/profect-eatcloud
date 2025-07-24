package profect.eatcloud.Domain.User.Dto.response;

import lombok.Data;

@Data
public class OrderResponseDto {
    private String orderId;
    private String orderNumber;
    private String storeId;
    private String orderStatus;
    private String orderType;
    // Add more fields as needed
} 