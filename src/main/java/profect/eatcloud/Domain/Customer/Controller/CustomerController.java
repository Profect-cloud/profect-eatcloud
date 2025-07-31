package profect.eatcloud.Domain.Customer.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Customer.Dto.request.CustomerProfileUpdateRequestDto;
import profect.eatcloud.Domain.Customer.Dto.request.CustomerWithdrawRequestDto;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerProfileResponseDto;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerWithdrawResponseDto;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Service.CustomerService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "고객 프로필 관리 API")
public class CustomerController {

	private final CustomerService customerService;

	@Operation(summary = "1. 고객 프로필 조회", description = "인증된 고객의 프로필 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음")
	})
	@GetMapping("/profile")
	public ResponseEntity<CustomerProfileResponseDto> getCustomer(
		@AuthenticationPrincipal UserDetails userDetails) {
		UUID customerId = getCustomerUuid(userDetails);
		Customer customer = customerService.getCustomer(customerId);
		CustomerProfileResponseDto response = CustomerProfileResponseDto.from(customer);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "2. 고객 프로필 수정", description = "인증된 고객의 프로필 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음")
	})
	@PatchMapping("/profile")
	public ResponseEntity<CustomerProfileResponseDto> updateCustomer(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody CustomerProfileUpdateRequestDto request) {
		UUID customerId = getCustomerUuid(userDetails);
		CustomerProfileResponseDto response = customerService.updateCustomer(customerId, request);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "3. 고객 탈퇴", description = "인증된 고객이 서비스에서 탈퇴합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "탈퇴 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음")
	})
	@PostMapping("/withdraw")
	public ResponseEntity<CustomerWithdrawResponseDto> withdrawCustomer(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody CustomerWithdrawRequestDto request) {
		UUID customerId = getCustomerUuid(userDetails);
		CustomerWithdrawResponseDto response = customerService.withdrawCustomer(customerId, request);
		return ResponseEntity.ok(response);
	}

	private UUID getCustomerUuid(UserDetails userDetails) {
		return UUID.fromString(userDetails.getUsername());
	}
}