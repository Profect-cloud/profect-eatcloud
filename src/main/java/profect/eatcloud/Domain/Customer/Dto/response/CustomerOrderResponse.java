package profect.eatcloud.Domain.Customer.Dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import profect.eatcloud.Domain.Order.Dto.OrderMenu;
import profect.eatcloud.Domain.Order.Entity.Order;

import java.util.List;
import java.util.UUID;

@Data
@Getter
public class CustomerOrderResponse {
    private String orderNumber;
    private List<OrderMenu> orderMenuList;
    private UUID customerId;
    private UUID storeId;
    private String orderStatus;
    private String orderType;

    public CustomerOrderResponse(Order order) {
        this.orderNumber = order.getOrderNumber();
        this.orderMenuList = order.getOrderMenuList();
        this.customerId = order.getCustomerId();
        this.storeId = order.getStoreId();
        this.orderStatus = order.getOrderStatusCode().getDisplayName();
        this.orderType = order.getOrderTypeCode().getDisplayName();
    }
}