package profect.eatcloud.Domain.Order.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AdminOrderCompleteRequestDto {
    private UUID orderId;
}
