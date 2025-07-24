package profect.eatcloud.Domain.Order.Dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class OrderRequestDto {
    private String orderNumber;
    private String userId;
    private UUID storeId;
    private UUID paymentId;
    private String orderStatus;
    private String orderType;
    private String orderMenuList; // jsonb는 String으로 처리
    private UUID pTimeId;
}
