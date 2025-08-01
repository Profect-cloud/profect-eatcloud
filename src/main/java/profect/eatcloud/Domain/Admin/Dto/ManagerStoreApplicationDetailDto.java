package profect.eatcloud.Domain.Admin.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin용: 단일 신청서의 세부 정보를 담는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "매니저·스토어 신청서 세부 정보")
public class ManagerStoreApplicationDetailDto {

	@Schema(description = "신청 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID applicationId;

	// Manager 신청 정보
	@Schema(description = "매니저 이름", example = "홍길동")
	private String managerName;

	@Schema(description = "매니저 이메일", example = "mgr@example.com")
	private String managerEmail;

	@Schema(description = "매니저 연락처", example = "010-1234-5678")
	private String managerPhoneNumber;

	// Store 신청 정보
	@Schema(description = "스토어 이름", example = "우리동네피자")
	private String storeName;

	@Schema(description = "스토어 주소", example = "서울시 강남구 ...")
	private String storeAddress;

	@Schema(description = "스토어 연락처", example = "02-123-4567")
	private String storePhoneNumber;

	@Schema(description = "카테고리 ID", example = "11111111-1111-1111-1111-111111111111")
	private UUID categoryId;

	@Schema(description = "스토어 설명")
	private String description;

	// 심사 상태
	@Schema(description = "신청 상태", example = "PENDING")
	private String status;

	@Schema(description = "심사자(Admin) ID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID reviewerAdminId;

	@Schema(description = "심사 코멘트")
	private String reviewComment;

	// 시간 정보
	@Schema(description = "신청 일시")
	private LocalDateTime appliedAt;

	@Schema(description = "최종 업데이트 일시")
	private LocalDateTime updatedAt;
}

