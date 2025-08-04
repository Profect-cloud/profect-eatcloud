package profect.eatcloud.domain.admin.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import profect.eatcloud.common.ApiResponse;
import profect.eatcloud.domain.admin.dto.DashboardDto;
import profect.eatcloud.domain.admin.dto.ManagerDto;
import profect.eatcloud.domain.admin.dto.StoreDto;
import profect.eatcloud.domain.admin.dto.UserDto;
import profect.eatcloud.domain.admin.message.ResponseMessage;
import profect.eatcloud.domain.admin.service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "2-1. admin API", description = "관리자만 사용하는 API")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminController {

	private final AdminService adminService;

	private UUID getAdminUuid(@AuthenticationPrincipal UserDetails userDetails) {
		return UUID.fromString(userDetails.getUsername());
	}

	@Operation(summary = "1-1. 전체 사용자 목록 조회")
	@GetMapping("/customers")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<UserDto>> getAllCustomers() {
		List<UserDto> users = adminService.getAllCustomers();
		return ApiResponse.success(users);
	}

	@Operation(summary = "1-2. 이메일로 Customer 조회")
	@GetMapping(value = "/customer/{email}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<UserDto> getCustomerByEmail(@PathVariable String email) {
		UserDto customer = adminService.getCustomerByEmail(email);
		return ApiResponse.success(customer);
	}

	@Operation(summary = "1-3. 이메일로 고객 밴(논리 삭제)")
	@DeleteMapping(value = "/customer/{email}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ResponseMessage> deleteCustomerByEmail(@PathVariable String email) {
		adminService.deleteCustomerByEmail(email);
		return ApiResponse.success(ResponseMessage.CUSTOMER_BAN_SUCCESS);
	}

	@Operation(summary = "2-1. 전체 매니저 목록 조회")
	@GetMapping("/managers")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<ManagerDto>> getAllManagers() {
		List<ManagerDto> list = adminService.getAllManagers();
		return ApiResponse.success(list);
	}

	@Operation(summary = "2-2. 이메일로 매니저 조회")
	@GetMapping(value = "/manager/{email}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ManagerDto> getManagerByEmail(@PathVariable String email) {
		ManagerDto m = adminService.getManagerByEmail(email);
		return ApiResponse.success(m);
	}

	@Operation(summary = "2-3. 이메일로 매니저 밴(논리 삭제)")
	@DeleteMapping(value = "/manager/{email}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ResponseMessage> deleteManagerByEmail(@PathVariable String email) {
		adminService.deleteManagerByEmail(email);
		return ApiResponse.success(ResponseMessage.MANAGER_BAN_SUCCESS);
	}

	@Operation(summary = "3-1. 가게 목록 조회")
	@GetMapping("/stores")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<StoreDto>> getStores() {
		List<StoreDto> stores = adminService.getStores();
		return ApiResponse.success(stores);
	}

	@Operation(summary = "3-2. 가게 상세 조회")
	@GetMapping("/store/{storeId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<StoreDto> getStore(@PathVariable UUID storeId) {
		StoreDto store = adminService.getStore(storeId);
		return ApiResponse.success(store);
	}

	@Operation(summary = "3-3. 가게 삭제")
	@DeleteMapping("/store/{storeId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<ResponseMessage> deleteStore(@PathVariable UUID storeId) {
		adminService.deleteStore(storeId);
		return ApiResponse.success(ResponseMessage.STORE_DELETE_SUCCESS);
	}

	@Operation(summary = "4-1. 대시보드 통계 조회", description = "전체 고객, 매니저, 가게 수를 조회합니다.")
	@GetMapping("/dashboard")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<DashboardDto> getDashboardStats() {
		DashboardDto stats = adminService.getDashboardStats();
		return ApiResponse.success(stats);
	}
}
