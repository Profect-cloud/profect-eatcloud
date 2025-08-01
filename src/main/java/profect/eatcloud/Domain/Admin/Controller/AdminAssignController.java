package profect.eatcloud.Domain.Admin.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Dto.ManagerStoreApplicationDetailDto;
import profect.eatcloud.Domain.Admin.Dto.ManagerStoreApplicationSummaryDto;
import profect.eatcloud.Domain.Admin.Service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "1. Admin Assign API", description = "관리자가 신규등록 신청관리 API")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAssignController {

	private final AdminService adminService;

	private UUID getAdminUuid(@AuthenticationPrincipal UserDetails userDetails) {
		return UUID.fromString(userDetails.getUsername());
	}

	@Operation(summary = "1. Admin: 신청서 목록 조회")
	@GetMapping("/applies")
	public ResponseEntity<List<ManagerStoreApplicationSummaryDto>> listApplications(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		UUID adminUuid = getAdminUuid(userDetails);
		List<ManagerStoreApplicationSummaryDto> list = adminService.getAllApplications(adminUuid);
		return ResponseEntity.ok(list);
	}

	@Operation(summary = "2. 신청서 상세 조회")
	@GetMapping("/{applicationId}")
	public ResponseEntity<ManagerStoreApplicationDetailDto> getDetail(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID applicationId
	) {
		UUID adminUuid = getAdminUuid(userDetails);
		ManagerStoreApplicationDetailDto dto = adminService.getApplicationDetail(adminUuid, applicationId);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "3. 신청서 승인")
	@PatchMapping("/{applicationId}/approve")
	public ResponseEntity<Void> approve(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID applicationId) {
		UUID adminUuid = getAdminUuid(userDetails);
		adminService.approveApplication(adminUuid, applicationId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "4. 신청서 거절")
	@PatchMapping("/{applicationId}/reject")
	public ResponseEntity<Void> reject(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID applicationId) {
		UUID adminUuid = getAdminUuid(userDetails);
		adminService.rejectApplication(adminUuid, applicationId);
		return ResponseEntity.noContent().build();
	}
}
