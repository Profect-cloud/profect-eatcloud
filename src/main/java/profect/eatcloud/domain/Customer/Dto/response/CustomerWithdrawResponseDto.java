package profect.eatcloud.domain.Customer.Dto.response;

import java.time.LocalDateTime;

public record CustomerWithdrawResponseDto(
	String message,
	LocalDateTime withdrawnAt
) {
	public static CustomerWithdrawResponseDto of(LocalDateTime withdrawnAt) {
		return new CustomerWithdrawResponseDto(
			"회원 탈퇴가 완료되었습니다.",
			withdrawnAt
		);
	}
}