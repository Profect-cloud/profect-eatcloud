package profect.eatcloud.Domain.Customer.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import profect.eatcloud.Domain.Customer.Dto.request.AddressRequestDto;
import profect.eatcloud.Domain.Customer.Dto.response.AddressResponseDto;
import profect.eatcloud.Domain.Customer.Service.AddressService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mypage/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "배송지 관리 API")
public class AddressController {

	private final AddressService addressService;

	@Operation(summary = "1. 배송지 목록 조회", description = "사용자의 모든 배송지를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@GetMapping
	public ResponseEntity<List<AddressResponseDto>> getAddressList(
		@AuthenticationPrincipal UserDetails userDetails) {
		UUID customerId = getCustomerUuid(userDetails);
		List<AddressResponseDto> addresses = addressService.getAddressList(customerId);
		return ResponseEntity.ok(addresses);
	}

	@Operation(summary = "2. 배송지 등록", description = "새로운 배송지를 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@PostMapping
	public ResponseEntity<AddressResponseDto> createAddress(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody AddressRequestDto request) {
		UUID customerId = getCustomerUuid(userDetails);
		AddressResponseDto response = addressService.createAddress(customerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "3. 배송지 수정", description = "기존 배송지 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음")
	})
	@PutMapping("/{addressId}")
	public ResponseEntity<AddressResponseDto> updateAddress(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID addressId,
		@Valid @RequestBody AddressRequestDto request) {
		UUID customerId = getCustomerUuid(userDetails);
		AddressResponseDto response = addressService.updateAddress(customerId, addressId, request);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "4. 배송지 삭제", description = "배송지를 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음")
	})
	@DeleteMapping("/{addressId}")
	public ResponseEntity<Void> deleteAddress(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID addressId) {
		UUID customerId = getCustomerUuid(userDetails);
		addressService.deleteAddress(customerId, addressId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "5. 기본 배송지 설정", description = "선택한 배송지를 기본 배송지로 설정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "설정 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음")
	})
	@PutMapping("/{addressId}/select")
	public ResponseEntity<Void> setDefaultAddress(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID addressId) {
		UUID customerId = getCustomerUuid(userDetails);
		addressService.setDefaultAddress(customerId, addressId);
		return ResponseEntity.noContent().build();
	}

	private UUID getCustomerUuid(UserDetails userDetails) {
		return UUID.fromString(userDetails.getUsername());
	}
}