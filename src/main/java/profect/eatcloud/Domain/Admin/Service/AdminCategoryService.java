package profect.eatcloud.Domain.Admin.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import profect.eatcloud.Domain.Admin.dto.CategoryDto;
import profect.eatcloud.Domain.Admin.entity.Admin;
import profect.eatcloud.Domain.Admin.repository.AdminRepository;
import profect.eatcloud.Domain.Store.Entity.Category;
import profect.eatcloud.Domain.Store.Repository.CategoryRepository_hong;

@Service
@AllArgsConstructor
public class AdminCategoryService {

	private final AdminRepository adminRepository;
	private final CategoryRepository_hong categoryRepository;

	@Transactional
	public CategoryDto createCategory(UUID adminUuid, CategoryDto dto) {
		// 관리자 유효성 확인
		Admin admin = adminRepository.findById(adminUuid)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

		// 카테고리 생성
		Category category = Category.builder()
			.categoryId(UUID.randomUUID())
			.categoryName(dto.getCategoryName())
			.sortOrder(dto.getSortOrder())
			.isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
			.build();

		categoryRepository.save(category);
		return dto;
	}

	@Transactional
	public CategoryDto updateCategory(UUID adminId, CategoryDto dto) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

		Category category = categoryRepository.findById(dto.getCategoryId())
			.orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

		category.setCategoryName(dto.getCategoryName());
		category.setSortOrder(dto.getSortOrder());
		category.setIsActive(dto.getIsActive());
		return dto;
	}

	@Transactional
	public void deleteCategory(UUID adminId, UUID categoryId) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

		categoryRepository.deleteById(category.getCategoryId());
	}
}
