package profect.eatcloud.domain.admin.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import profect.eatcloud.domain.admin.dto.CategoryDto;
import profect.eatcloud.domain.admin.service.AdminCategoryService;
import profect.eatcloud.common.ApiResponse;
import profect.eatcloud.common.ApiResponseStatus;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/categories")
@Tag(name = "2-2. Admin Category API", description = "관리자가 카테고리 CRUD를 수행하는 API")
public class AdminCategoryController {

	private final AdminCategoryService adminCategoryService;

	@Operation(summary = "1. 카테고리 생성")
	@PostMapping("/add")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
		CategoryDto created = adminCategoryService.createCategory(dto);
		return ApiResponse.of(ApiResponseStatus.CREATED, created);
	}

	@Operation(summary = "2. 카테고리 수정")
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<CategoryDto> updateCategory(@PathVariable UUID id, @RequestBody CategoryDto dto) {
		CategoryDto updated = adminCategoryService.updateCategory(id, dto);
		return ApiResponse.success(updated);
	}

	@Operation(summary = "3. 카테고리 삭제")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<Void> deleteCategory(@PathVariable UUID id) {
		adminCategoryService.deleteCategory(id);
		return ApiResponse.success();
	}
}
