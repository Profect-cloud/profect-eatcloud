package profect.eatcloud.domain.admin.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import profect.eatcloud.domain.admin.dto.CategoryDto;
import profect.eatcloud.domain.admin.exception.AdminErrorCode;
import profect.eatcloud.domain.admin.exception.AdminException;
import profect.eatcloud.domain.admin.repository.AdminRepository;
import profect.eatcloud.domain.store.entity.Category;
import profect.eatcloud.domain.Store.Repository.CategoryRepository_hong;

@Service
@AllArgsConstructor
public class AdminCategoryService {

	private final AdminRepository adminRepository;
	private final CategoryRepository_hong categoryRepository;

	@Transactional
	public CategoryDto createCategory(CategoryDto dto) {
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
	public CategoryDto updateCategory(UUID id, CategoryDto dto) {
		Category category = categoryRepository.findById(id)
			.orElseThrow(() -> new AdminException(AdminErrorCode.CATEGORY_NOT_FOUND));

		category.setCategoryName(dto.getCategoryName());
		category.setSortOrder(dto.getSortOrder());
		category.setIsActive(dto.getIsActive());
		return dto;
	}

	@Transactional
	public void deleteCategory(UUID categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new AdminException(AdminErrorCode.CATEGORY_NOT_FOUND));

		categoryRepository.delete(category);
	}
}
