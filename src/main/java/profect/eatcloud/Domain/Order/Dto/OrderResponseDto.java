package profect.eatcloud.Domain.Order.Dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class OrderResponseDto {
    private UUID orderId;
    private String orderNumber;
    private String userId;
    private UUID storeId;
    private UUID paymentId;
    private String orderStatus;
    private String orderType;
    private String orderMenuList; // jsonb는 String으로 처리
    private UUID pTimeId;
}