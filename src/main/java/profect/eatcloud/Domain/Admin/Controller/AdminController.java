package profect.eatcloud.Domain.Admin.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import profect.eatcloud.Domain.Admin.dto.ManagerDto;
import profect.eatcloud.Domain.Admin.dto.StoreDto;
import profect.eatcloud.Domain.Admin.dto.UserDto;
import profect.eatcloud.Domain.Admin.message.ResponseMessage;
import profect.eatcloud.Domain.Admin.service.AdminService;
import profect.eatcloud.common.ApiResponse;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "1. Admin API", description = "관리자만 사용하는 API")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminController {

	private final AdminService adminService;

	private UUID getAdminUuid(@AuthenticationPrincipal UserDetails userDetails) {
		return UUID.fromString(userDetails.getUsername());
	}

	@Operation(summary = "1-1. 전체 사용자 목록 조회")
	@GetMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<UserDto>> getAllCustomers(
		@AuthenticationPrincipal UserDetails userDetails) {

		UUID adminUuid = getAdminUuid(userDetails);
		List<UserDto> users = adminService.getAllCustomers(adminUuid);
		return ApiResponse.success(users);
	}

	@Operation(summary = "1-2. 이메일로 Customer 조회")
	@GetMapping(value = "/users/search", params = "email")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<UserDto> getCustomerByEmail(@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam String email) {

		UUID adminUuid = getAdminUuid(userDetails);
		UserDto customer = adminService.getCustomerByEmail(adminUuid, email);
		return ApiResponse.success(customer);
	}

	@Operation(summary = "1-3. 이메일로 고객 밴(논리 삭제)")
	@DeleteMapping(value = "/customers", params = "email")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ResponseMessage> deleteCustomerByEmail(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam String email) {

		UUID adminUuid = getAdminUuid(userDetails);
		adminService.deleteCustomerByEmail(adminUuid, email);
		return ApiResponse.success(ResponseMessage.CUSTOMER_BAN_SUCCESS);
	}

	@Operation(summary = "2-1. 전체 매니저 목록 조회")
	@GetMapping("/managers")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<ManagerDto>> getAllManagers(
		@AuthenticationPrincipal UserDetails userDetails) {

		UUID adminUuid = getAdminUuid(userDetails);
		List<ManagerDto> list = adminService.getAllManagers(adminUuid);
		return ApiResponse.success(list);
	}

	@Operation(summary = "2-2. 이메일로 매니저 조회")
	@GetMapping(value = "/managers/search", params = "email")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ManagerDto> getManagerByEmail(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam String email) {

		UUID adminUuid = getAdminUuid(userDetails);
		ManagerDto m = adminService.getManagerByEmail(adminUuid, email);
		return ApiResponse.success(m);
	}

	@Operation(summary = "2-3. 이메일로 매니저 밴(논리 삭제)")
	@DeleteMapping(value = "/managers", params = "email")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ResponseMessage> deleteManagerByEmail(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam String email) {

		UUID adminUuid = getAdminUuid(userDetails);
		adminService.deleteManagerByEmail(adminUuid, email);
		return ApiResponse.success(ResponseMessage.MANAGER_BAN_SUCCESS);
	}

	@Operation(summary = "3-1. 가게 목록 조회")
	@GetMapping("/stores")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<StoreDto>> getStores(
		@AuthenticationPrincipal UserDetails userDetails) {

		UUID adminUuid = getAdminUuid(userDetails);
		List<StoreDto> stores = adminService.getStores(adminUuid);
		return ApiResponse.success(stores);
	}

	@Operation(summary = "3-3. 가게 삭제")
	@DeleteMapping("/stores/{storeId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ResponseMessage> deleteStore(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long storeId) {

		adminService.deleteStore(userDetails.getUsername(), storeId);
		return ApiResponse.success(ResponseMessage.STORE_DELETE_SUCCESS);
	}

	//------------------------------------------------------------

}
