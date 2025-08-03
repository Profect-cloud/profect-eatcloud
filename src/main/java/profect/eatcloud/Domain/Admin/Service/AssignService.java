package profect.eatcloud.Domain.Admin.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.dto.ManagerStoreApplicationRequestDto;
import profect.eatcloud.Domain.Admin.dto.ManagerStoreApplicationResponseDto;
import profect.eatcloud.Domain.Admin.entity.ManagerStoreApplication;
import profect.eatcloud.Domain.Admin.repository.ManagerStoreApplicationRepository;

@Service
@RequiredArgsConstructor
public class AssignService {

	private final ManagerStoreApplicationRepository managerStoreApplicationRepository;

	@Transactional
	public ManagerStoreApplicationResponseDto newManagerStoreApply(ManagerStoreApplicationRequestDto req) {
		ManagerStoreApplication entity = ManagerStoreApplication.builder()
			.applicationId(UUID.randomUUID())
			.managerName(req.getManagerName())
			.managerEmail(req.getManagerEmail())
			.managerPassword(req.getManagerPassword())
			.managerPhoneNumber(req.getManagerPhoneNumber())
			.storeName(req.getStoreName())
			.storeAddress(req.getStoreAddress())
			.storePhoneNumber(req.getStorePhoneNumber())
			.categoryId(req.getCategoryId())
			.description(req.getDescription())
			.status("PENDING")
			.build();

		ManagerStoreApplication saved = managerStoreApplicationRepository.save(entity);

		return ManagerStoreApplicationResponseDto.builder()
			.applicationId(saved.getApplicationId())
			.status(saved.getStatus())
			.createdAt(saved.getTimeData().getCreatedAt())
			.build();
	}

}