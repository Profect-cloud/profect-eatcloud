package profect.eatcloud.Domain.Admin.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import profect.eatcloud.Domain.Admin.dto.CategoryDto;
import profect.eatcloud.Domain.Admin.service.AdminCategoryService;
import profect.eatcloud.common.ApiResponse;
import profect.eatcloud.common.ApiResponseStatus;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/categories")
@Tag(name = "1-3. Admin Category API", description = "관리자가 카테고리 CRUD를 수행하는 API")
public class AdminCategoryController {

	private final AdminCategoryService adminCategoryService;

	private UUID getAdminUuid(@AuthenticationPrincipal UserDetails userDetails) {
		return UUID.fromString(userDetails.getUsername());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<CategoryDto> createCategory(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody CategoryDto dto) {

		UUID adminUuid = getAdminUuid(userDetails);
		CategoryDto created = adminCategoryService.createCategory(adminUuid, dto);
		return ApiResponse.of(ApiResponseStatus.CREATED, created);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<CategoryDto> updateCategory(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID id,
		@RequestBody CategoryDto dto) {

		UUID adminUuid = getAdminUuid(userDetails);
		dto.setCategoryId(id);
		CategoryDto updated = adminCategoryService.updateCategory(adminUuid, dto);
		return ApiResponse.success(updated);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<Void> deleteCategory(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable UUID id) {

		UUID adminUuid = getAdminUuid(userDetails);
		adminCategoryService.deleteCategory(adminUuid, id);
		return ApiResponse.success();
	}
}
