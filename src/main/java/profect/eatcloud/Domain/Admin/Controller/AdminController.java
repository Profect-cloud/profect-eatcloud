package profect.eatcloud.Domain.Admin.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Dto.CategoryDto;
import profect.eatcloud.Domain.Admin.Dto.DashboardDto;
import profect.eatcloud.Domain.Admin.Dto.OrderDto;
import profect.eatcloud.Domain.Admin.Dto.OrderStatusDto;
import profect.eatcloud.Domain.Admin.Dto.StoreDto;
import profect.eatcloud.Domain.Admin.Dto.UserDto;
import profect.eatcloud.Domain.Admin.Service.AdminService;

/**
 * Admin 전용 API 컨트롤러 (뼈대)
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin API", description = "관리자 전용 API 모음")
@RequiredArgsConstructor
public class AdminController {

	/*
	 1. 전체 사용자 목록 조회
	 2. 사용자 삭제
	 3. 가게 등록
	 4. 가게 목록 조회
	 5. 가게 정보 변경
	 6. 가게 삭제
	 7. 카테고리 추가
	 8. 카테고리 변경
	 9. 카테고리 삭제
	 10. 주문 상세 조회
	 11. 주문 상태 변경
	 12. 대시보드 조회
	*/
	private final AdminService adminService;

	// 사용자가 많아질 때 어떻게 로직을 변경할지 고민해 봐야함.
	@Operation(summary = "1. 전체 사용자 목록 조회")
	@GetMapping("/users")
	public ResponseEntity<List<UserDto>> getAllCustomers(
		@AuthenticationPrincipal UserDetails userDetails) {
		String adminId = userDetails.getUsername();
		List<UserDto> users = adminService.getAllCustomers(adminId);
		return ResponseEntity.ok(users);
	}

	@Operation(summary = "2. 사용자 삭제")
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID userId) {
		String adminId = userDetails.getUsername();
		adminService.deleteCustomer(adminId, userId);
		return ResponseEntity.ok("사용자 삭제 완료");
	}

	@Operation(summary = "3. 가게 등록")
	@PostMapping("/stores")
	public ResponseEntity<StoreDto> createStore(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody StoreDto storeDto
	) {
		String adminId = userDetails.getUsername();
		StoreDto created = adminService.createStore(adminId, storeDto);
		return ResponseEntity.ok(created);
	}

	@Operation(summary = "4. 가게 목록 조회")
	@GetMapping("/stores")
	public ResponseEntity<List<StoreDto>> getStores(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		String adminId = userDetails.getUsername();
		List<StoreDto> stores = adminService.getStores(adminId);
		return ResponseEntity.ok(stores);
	}

	@Operation(summary = "5. 가게 정보 변경")
	@PatchMapping("/stores/{storeId}")
	public ResponseEntity<StoreDto> updateStore(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long storeId,
		@RequestBody StoreDto storeDto
	) {
		String adminId = userDetails.getUsername();
		StoreDto updated = adminService.updateStore(adminId, storeId, storeDto);
		return ResponseEntity.ok(updated);
	}

	@Operation(summary = "6. 가게 삭제")
	@DeleteMapping("/stores/{storeId}")
	public ResponseEntity<String> deleteStore(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long storeId
	) {
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
