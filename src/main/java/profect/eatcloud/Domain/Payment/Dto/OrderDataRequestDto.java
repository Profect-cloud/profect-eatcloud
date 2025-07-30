package profect.eatcloud.Domain.Payment.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 데이터 요청")
public class OrderDataRequestDto {

    @Schema(
        description = "고객 ID (UUID 형식)",
        example = "550e8400-e29b-41d4-a716-446655440000",
        required = true
    )
    private String customerId;

    @Schema(
        description = "주문 메뉴 목록",
        required = true
    )
    private List<OrderItemDto> items;

    @Schema(
        description = "총 주문 금액 (원 단위, 배달비 포함)",
        example = "28000",
        required = true
    )
    private Integer totalAmount;

    @Schema(
        description = "포인트 사용 여부",
        example = "true"
    )
    private Boolean usePoints;

    @Schema(
        description = "사용할 포인트 금액",
        example = "5000"
    )
    private Integer pointsToUse;

    @Schema(
        description = "최종 결제 금액 (포인트 차감 후)",
        example = "23000"
    )
    private Integer finalPaymentAmount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주문 메뉴 항목")
    public static class OrderItemDto {

        @Schema(
            description = "메뉴 ID",
            example = "kimchi"
        )
        private String menuId;

        @Schema(
            description = "메뉴명",
            example = "김치찌개"
        )
        private String name;

        @Schema(
            description = "메뉴 가격 (원 단위)",
            example = "8000"
        )
        private Integer price;

        @Schema(
            description = "주문 수량",
            example = "2"
        )
        private Integer quantity;
    }
} 