package profect.eatcloud.Domain.Admin.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Dto.CategoryDto;
import profect.eatcloud.Domain.Admin.Dto.DashboardDto;
import profect.eatcloud.Domain.Admin.Dto.ManagerDto;
import profect.eatcloud.Domain.Admin.Dto.OrderDto;
import profect.eatcloud.Domain.Admin.Dto.OrderStatusDto;
import profect.eatcloud.Domain.Admin.Dto.StoreDto;
import profect.eatcloud.Domain.Admin.Dto.UserDto;
import profect.eatcloud.Domain.Admin.Service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "1. Admin API", description = "관리자만 사용하는 API")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	private UUID getAdminUuid(@AuthenticationPrincipal UserDetails userDetails) {
		return UUID.fromString(userDetails.getUsername());
	}

	// ---------------- GET ---------------
	@Operation(summary = "1-1. 전체 Customer 목록 조회")
	@GetMapping("/users")
	public ResponseEntity<List<UserDto>> getAllCustomers(@AuthenticationPrincipal UserDetails userDetails) {
		UUID adminUuid = getAdminUuid(userDetails);
		return ResponseEntity.ok(adminService.getAllCustomers(adminUuid));
	}

	@Operation(summary = "1-2. 이메일로 Customer 조회")
	@GetMapping(value = "/users/search", params = "email")
	public ResponseEntity<UserDto> getCustomerByEmail(@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam String email) {
		UUID adminUuid = getAdminUuid(userDetails);
		return ResponseEntity.ok(adminService.getCustomerByEmail(adminUuid, email));
	}

	@Operation(summary = "1-3. 이메일로 고객 밴(논리 삭제)")
	@DeleteMapping(value = "/customers", params = "email")
	public ResponseEntity<String> deleteCustomerByEmail(@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam String email) {
		UUID adminUuid = getAdminUuid(userDetails);
		adminService.deleteCustomerByEmail(adminUuid, email);
		return ResponseEntity.ok("고객 밴 처리 완료: " + email);
	}

	@Operation(summary = "2-1. 전체 매니저 목록 조회")
	@GetMapping("/managers")
	public ResponseEntity<List<ManagerDto>> getAllManagers(@AuthenticationPrincipal UserDetails userDetails) {
		UUID adminUuid = getAdminUuid(userDetails);
		return ResponseEntity.ok(adminService.getAllManagers(adminUuid));
	}

	@Operation(summary = "2-2. 이메일로 매니저 조회")
	@GetMapping(value = "/managers/search", params = "email")
	public ResponseEntity<ManagerDto> getManagerByEmail(@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam String email) {
		UUID adminUuid = getAdminUuid(userDetails);
		return ResponseEntity.ok(adminService.getManagerByEmail(adminUuid, email));
	}

	@Operation(summary = "2-3. 이메일로 매니저 밴(논리 삭제)")
	@DeleteMapping(value = "/managers", params = "email")
	public ResponseEntity<String> deleteManagerByEmail(@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam String email) {

		UUID adminUuid = getAdminUuid(userDetails);
		adminService.deleteManagerByEmail(adminUuid, email);
		return ResponseEntity.ok("매니저 밴 처리 완료: " + email);
	}

	@Operation(summary = "3-1. 가게 목록 조회")
	@GetMapping("/stores")
	public ResponseEntity<List<StoreDto>> getStores(@AuthenticationPrincipal UserDetails userDetails) {
		UUID adminUuid = getAdminUuid(userDetails);
		List<StoreDto> stores = adminService.getStores(adminUuid);
		return ResponseEntity.ok(stores);
	}

	@Operation(summary = "3-3. 가게 삭제")
	@DeleteMapping("/stores/{storeId}")
	public ResponseEntity<String> deleteStore(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long storeId) {
		String adminId = userDetails.getUsername();
		adminService.deleteStore(adminId, storeId);
		return ResponseEntity.ok("가게 삭제 완료");
	}

	@Operation(summary = "7. 카테고리 추가")
	@PostMapping("/categories")
	public ResponseEntity<CategoryDto> createCategory(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody CategoryDto categoryDto
	) {
		String adminId = userDetails.getUsername();
		CategoryDto created = adminService.createCategory(adminId, categoryDto);
		return ResponseEntity.ok(created);
	}

	@Operation(summary = "8. 카테고리 변경")
	@PatchMapping("/categories/{categoryId}")
	public ResponseEntity<CategoryDto> updateCategory(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long categoryId,
		@RequestBody CategoryDto categoryDto
	) {
		String adminId = userDetails.getUsername();
		CategoryDto updated = adminService.updateCategory(adminId, categoryId, categoryDto);
		return ResponseEntity.ok(updated);
	}

	@Operation(summary = "9. 카테고리 삭제")
	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<String> deleteCategory(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long categoryId
	) {
		String adminId = userDetails.getUsername();
		adminService.deleteCategory(adminId, categoryId);
		return ResponseEntity.ok("카테고리 삭제 완료");
	}

	@Operation(summary = "10. 주문 상세 조회")
	@GetMapping("/orders/{orderId}")
	public ResponseEntity<OrderDto> getOrderDetail(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long orderId
	) {
		String adminId = userDetails.getUsername();
		OrderDto order = adminService.getOrderDetail(adminId, orderId);
		return ResponseEntity.ok(order);
	}

	@Operation(summary = "11. 주문 상태 변경")
	@PatchMapping("/orders/{orderId}/status")
	public ResponseEntity<OrderDto> updateOrderStatus(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long orderId, @RequestBody OrderStatusDto statusDto
	) {
		String adminId = userDetails.getUsername();
		OrderDto updated = adminService.updateOrderStatus(adminId, orderId, statusDto);
		return ResponseEntity.ok(updated);
	}

	@Operation(summary = "12. 대시보드 조회")
	@GetMapping("/dashboard")
	public ResponseEntity<DashboardDto> getDashboard(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		String adminId = userDetails.getUsername();
		DashboardDto data = adminService.getDashboard(adminId);
		return ResponseEntity.ok(data);
	}
}
