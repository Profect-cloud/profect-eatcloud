package profect.eatcloud.Domain.Customer.Dto.response;

import java.time.LocalDateTime;

public record CustomerWithdrawResponse(
	String message,
	LocalDateTime withdrawnAt
) {
	public static CustomerWithdrawResponse of(LocalDateTime withdrawnAt) {
		return new CustomerWithdrawResponse(
			"회원 탈퇴가 완료되었습니다.",
			withdrawnAt
		);
	}
}