package profect.eatcloud.Domain.Admin.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Dto.ManagerStoreApplicationRequestDto;
import profect.eatcloud.Domain.Admin.Dto.ManagerStoreApplicationResponseDto;
import profect.eatcloud.Domain.Admin.Service.AssignService;

@RestController
@RequestMapping("/api/v1/unauth")
@Tag(name = "1. Unauth API", description = "로그인 없이, Admin에게 신청 요청하는 API")
@RequiredArgsConstructor

public class AssignController {

	private final AssignService assignService;

	@Operation(summary = "매니저·스토어 신청하기")
	@PostMapping("/manager-apply")
	public ResponseEntity<ManagerStoreApplicationResponseDto> apply(
		@RequestBody ManagerStoreApplicationRequestDto request) {

		ManagerStoreApplicationResponseDto resp = assignService.newManagerStoreApply(request);
		return ResponseEntity.status(201).body(resp);
	}
}
