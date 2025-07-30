package profect.eatcloud.Domain.Payment.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "결제 페이지 응답")
public class CheckoutResponseDto {

    @Schema(
        description = "토스페이먼츠 주문 ID",
        example = "ORDER_A1B2C3D4"
    )
    private String orderId;

    @Schema(
        description = "최종 결제 금액 (원 단위)",
        example = "25000"
    )
    private Integer amount;

    @Schema(
        description = "고객 ID",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String userId;

    @Schema(
        description = "토스페이먼츠 클라이언트 키",
        example = "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq"
    )
    private String clientKey;

    @Schema(
        description = "포인트 사용 여부",
        example = "true"
    )
    private Boolean usePoints;

    @Schema(
        description = "사용한 포인트 금액",
        example = "5000"
    )
    private Integer pointsUsed;

    @Schema(
        description = "원래 주문 금액",
        example = "30000"
    )
    private Integer originalAmount;

    @Schema(
        description = "주문 메뉴 목록",
        example = "[{\"menuId\":\"kimchi\",\"name\":\"김치찌개\",\"price\":8000,\"quantity\":2}]"
    )
    private List<Map<String, Object>> orderItems;

    @Schema(
        description = "응답 메시지",
        example = "결제 페이지 데이터 생성 성공"
    )
    private String message;

    @Schema(
        description = "경고 메시지 (선택사항)",
        example = "고객 ID가 UUID 형식이 아닙니다. 포인트 사용을 건너뜁니다."
    )
    private String warning;

    @Schema(
        description = "오류 메시지 (선택사항)",
        example = "포인트 잔액이 부족합니다."
    )
    private String error;
} 