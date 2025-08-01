package profect.eatcloud.Domain.Admin.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Dto.CategoryDto;
import profect.eatcloud.Domain.Admin.Dto.DashboardDto;
import profect.eatcloud.Domain.Admin.Dto.ManagerCreateRequestDto;
import profect.eatcloud.Domain.Admin.Dto.ManagerDto;
import profect.eatcloud.Domain.Admin.Dto.ManagerStoreApplicationDetailDto;
import profect.eatcloud.Domain.Admin.Dto.ManagerStoreApplicationSummaryDto;
import profect.eatcloud.Domain.Admin.Dto.OrderDto;
import profect.eatcloud.Domain.Admin.Dto.OrderStatusDto;
import profect.eatcloud.Domain.Admin.Dto.StoreDto;
import profect.eatcloud.Domain.Admin.Dto.UserDto;
import profect.eatcloud.Domain.Admin.Entity.ManagerStoreApplication;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Admin.Repository.ManagerStoreApplicationRepository;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Entity.Manager;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;
import profect.eatcloud.Domain.Order.Repository.OrderRepository;
import profect.eatcloud.Domain.Store.Entity.Category;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Store.Repository.CategoryRepository_hong;
import profect.eatcloud.Domain.Store.Repository.StoreRepository_hong;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminRepository adminRepository;        // p_admins
	private final CustomerRepository customerRepository;  // p_customer
	private final ManagerRepository managerRepository;
	private final StoreRepository_hong storeRepository;        // p_stores
	private final CategoryRepository_hong categoryRepository;  // p_categories
	private final OrderRepository orderRepository;        // p_orders
	private final PasswordEncoder passwordEncoder;
	private final ManagerStoreApplicationRepository managerStoreApplicationRepository;

	@Transactional(readOnly = true)
	public List<UserDto> getAllCustomers(UUID adminUuid) {
		// 1) UUID 변환 및 Admin 검증
		adminRepository.findById(adminUuid)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminUuid));

		// 2) 전체 고객 조회 후 UserDto 로 매핑
		return customerRepository.findAll().stream()
			.map(c -> UserDto.builder()
				.id(c.getId())
				.name(c.getName())
				.nickname(c.getNickname())
				.email(c.getEmail())
				.phoneNumber(c.getPhoneNumber())
				.points(c.getPoints())
				.build()
			)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ManagerDto> getAllManagers(UUID adminId) {
		// 1) 관리자 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) 모든 매니저 조회 후 DTO 매핑
		return managerRepository.findAll().stream()
			.map(m -> ManagerDto.builder()
				.id(m.getId())
				.name(m.getName())
				.email(m.getEmail())
				.phoneNumber(m.getPhoneNumber())
				.storeId(m.getStore() != null ? m.getStore().getStoreId() : null)
				.build()
			)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public UserDto getCustomerByEmail(UUID adminId, String email) {
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		Customer c = customerRepository.findByEmail(email)
			.orElseThrow(() -> new NoSuchElementException("Customer not found: " + email));

		return UserDto.builder()
			.id(c.getId())
			.name(c.getName())
			.nickname(c.getNickname())
			.email(c.getEmail())
			.phoneNumber(c.getPhoneNumber())
			.points(c.getPoints())
			.build();
	}

	@Transactional(readOnly = true)
	public ManagerDto getManagerByEmail(UUID adminId, String email) {
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		Manager m = managerRepository.findByEmail(email)
			.orElseThrow(() -> new NoSuchElementException("Manager not found: " + email));

		return ManagerDto.builder()
			.id(m.getId())
			.name(m.getName())
			.email(m.getEmail())
			.phoneNumber(m.getPhoneNumber())
			.storeId(m.getStore() != null ? m.getStore().getStoreId() : null)
			.build();
	}

	@Transactional
	public void deleteCustomerByEmail(UUID adminId, String email) {
		// 1) Admin 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) 이메일로 고객 조회 (없으면 예외)
		Customer c = customerRepository.findByEmail(email)
			.orElseThrow(() -> new NoSuchElementException("Customer not found: " + email));

		// 3) 논리 삭제 (레포지토리 deleteById() 에서 처리)
		customerRepository.deleteById(c.getId());
	}

	@Transactional
	public void deleteManagerByEmail(UUID adminId, String email) {
		// 1) Admin 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) 이메일로 매니저 조회 (없으면 예외)
		Manager m = managerRepository.findByEmail(email)
			.orElseThrow(() -> new NoSuchElementException("Manager not found: " + email));

		// 3) 논리 삭제
		managerRepository.deleteById(m.getId());
	}

	@Transactional
	public ManagerDto createManager(UUID adminId, ManagerCreateRequestDto dto) {
		// 1) Admin 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) (선택) DTO에 storeId 있으면 검증 후 불러오기
		Store store = null;
		if (dto.getStoreId() != null) {
			store = storeRepository.findById(dto.getStoreId())
				.orElseThrow(() -> new NoSuchElementException("Store not found: " + dto.getStoreId()));
		}

		// 3) Manager 엔티티 생성
		Manager manager = Manager.builder()
			.id(UUID.randomUUID())
			.email(dto.getEmail())
			.password(passwordEncoder.encode(dto.getPassword()))
			.name(dto.getName())
			.phoneNumber(dto.getPhoneNumber())
			.store(store)     // store 가 null 이면 NPE 아님, 관계 없이 생성
			.build();

		Manager saved = managerRepository.save(manager);

		// 4) 응답 DTO로 변환
		return ManagerDto.builder()
			.id(saved.getId())
			.email(saved.getEmail())
			.name(saved.getName())
			.phoneNumber(saved.getPhoneNumber())
			.storeId(saved.getStore() != null
				? saved.getStore().getStoreId()
				: null)
			.build();
	}

	@Transactional(readOnly = true)
	public List<ManagerStoreApplicationSummaryDto> getAllApplications(UUID adminId) {
		// 1) Admin 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) 전체 신청서 조회 및 DTO 매핑
		return managerStoreApplicationRepository.findAll().stream()
			.map(app -> ManagerStoreApplicationSummaryDto.builder()
				.applicationId(app.getApplicationId())
				.managerName(app.getManagerName())
				.managerEmail(app.getManagerEmail())
				.storeName(app.getStoreName())
				.status(app.getStatus())
				.appliedAt(app.getTimeData().getCreatedAt())
				.build())
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public ManagerStoreApplicationDetailDto getApplicationDetail(UUID adminId, UUID applicationId) {
		// 1) Admin 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) 신청서 조회
		ManagerStoreApplication app = managerStoreApplicationRepository.findById(applicationId)
			.orElseThrow(() -> new NoSuchElementException("Application not found: " + applicationId));

		// 3) DTO 변환
		return ManagerStoreApplicationDetailDto.builder()
			.applicationId(app.getApplicationId())
			.managerName(app.getManagerName())
			.managerEmail(app.getManagerEmail())
			.managerPhoneNumber(app.getManagerPhoneNumber())
			.storeName(app.getStoreName())
			.storeAddress(app.getStoreAddress())
			.storePhoneNumber(app.getStorePhoneNumber())
			.categoryId(app.getCategoryId())
			.description(app.getDescription())
			.status(app.getStatus())
			.reviewerAdminId(app.getReviewerAdminId())
			.reviewComment(app.getReviewComment())
			.appliedAt(app.getTimeData().getCreatedAt())
			.updatedAt(app.getTimeData().getUpdatedAt())
			.build();
	}

	@Transactional
	public void approveApplication(UUID adminId, UUID applicationId) {
		// 1) Admin 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) Pending된 신청서만 조회
		ManagerStoreApplication app = managerStoreApplicationRepository.findById(applicationId)
			.orElseThrow(() -> new NoSuchElementException("Application not found: " + applicationId));
		if (!"PENDING".equals(app.getStatus())) {
			throw new IllegalStateException("이미 처리된 신청서입니다: " + app.getStatus());
		}

		// 3) Store 생성
		Store store = Store.builder()
			.storeId(UUID.randomUUID())
			.storeName(app.getStoreName())
			.storeAddress(app.getStoreAddress())
			.phoneNumber(app.getStorePhoneNumber())
			.category(app.getCategoryId() != null
				? categoryRepository.findById(app.getCategoryId())
				.orElseThrow(() -> new NoSuchElementException("Category not found: " + app.getCategoryId()))
				: null)
			.minCost(0)
			.description(app.getDescription())
			.openStatus(false)
			.openTime(null)
			.closeTime(null)
			.build();
		storeRepository.save(store);

		// 4) Manager 생성
		Manager mgr = Manager.builder()
			.id(UUID.randomUUID())
			.email(app.getManagerEmail())
			.password(passwordEncoder.encode(app.getManagerPassword()))
			.name(app.getManagerName())
			.phoneNumber(app.getManagerPhoneNumber())
			.store(store)
			.build();
		managerRepository.save(mgr);

		// 5) 신청서 상태 업데이트
		app.setStatus("APPROVED");
		app.setReviewerAdminId(adminId);
		managerStoreApplicationRepository.save(app);
		// BaseTimeEntity가 updatedAt/updatedBy 자동 처리
	}

	@Transactional
	public void rejectApplication(UUID adminId, UUID applicationId) {
		// 1) Admin 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) Pending된 신청서만 조회
		ManagerStoreApplication application = managerStoreApplicationRepository.findById(applicationId)
			.orElseThrow(() -> new NoSuchElementException("Application not found: " + applicationId));
		if (!"PENDING".equals(application.getStatus())) {
			throw new IllegalStateException("이미 처리된 신청서입니다: " + application.getStatus());
		}

		// 3) 신청서 상태만 REJECTED로 변경
		application.setStatus("REJECTED");
		application.setReviewerAdminId(adminId);

		// 4) 변경사항 저장 (updatedAt/updatedBy는 BaseTimeEntity가 자동 처리)
		managerStoreApplicationRepository.save(application);
	}

	@Transactional(readOnly = true)
	public List<StoreDto> getStores(UUID adminId) {
		// 1) Admin 검증
		adminRepository.findById(adminId)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

		// 2) 모든 가게 조회 및 DTO 매핑
		return storeRepository.findAll().stream()
			.map(s -> StoreDto.builder()
				.storeId(s.getStoreId())
				.storeName(s.getStoreName())
				.categoryId(s.getCategory() != null ? s.getCategory().getCategoryId() : null)
				.minCost(s.getMinCost())
				.description(s.getDescription())
				.storeLat(s.getStoreLat())
				.storeLon(s.getStoreLon())
				.openStatus(s.getOpenStatus())
				.openTime(s.getOpenTime())
				.closeTime(s.getCloseTime())
				.build()
			)
			.collect(Collectors.toList());
	}

	@Transactional
	public StoreDto createStore(String adminId, StoreDto storeDto) {
		// 1) 관리자 검증
		UUID adminUuid = UUID.fromString(adminId);
		adminRepository.findById(adminUuid);

		// 2) 카테고리 검증
		UUID categoryUuid = storeDto.getCategoryId();
		Category category = categoryRepository.findById(categoryUuid)
			.orElseThrow(() -> new NoSuchElementException("Category not found: " + categoryUuid));

		// 3) 엔티티 생성
		Store store = Store.builder()
			// DB에서 자동 생성되는 PK가 없다면 UUID.randomUUID() 사용
			.storeId(UUID.randomUUID())
			.storeName(storeDto.getStoreName())
			.description(storeDto.getDescription())
			.minCost(storeDto.getMinCost())
			.storeLat(storeDto.getStoreLat())
			.storeLon(storeDto.getStoreLon())
			.openStatus(storeDto.getOpenStatus())
			.openTime(storeDto.getOpenTime())
			.closeTime(storeDto.getCloseTime())
			.category(category)
			.build();

		// 4) 저장 (TimeDataListener가 p_time을 자동 생성/연결해 줍니다)
		Store saved = storeRepository.save(store);

		// 5) DTO로 변환하여 반환
		return StoreDto.builder()
			.storeId(saved.getStoreId())
			.storeName(saved.getStoreName())
			.categoryId(saved.getCategory().getCategoryId())
			.minCost(saved.getMinCost())
			.description(saved.getDescription())
			.storeLat(saved.getStoreLat())
			.storeLon(saved.getStoreLon())
			.openStatus(saved.getOpenStatus())
			.openTime(saved.getOpenTime())
			.closeTime(saved.getCloseTime())
			.build();
	}

	@Transactional
	public StoreDto updateStore(String adminId, Long storeId, StoreDto storeDto) {
		// TODO: 관리자의 권한 확인 및 가게 정보 업데이트
		return null;
	}

	@Transactional
	public void deleteStore(String adminId, Long storeId) {
		// TODO: 관리자의 권한 확인 및 가게 삭제 처리
	}

	@Transactional
	public CategoryDto createCategory(String adminId, CategoryDto categoryDto) {
		// TODO: 관리자의 권한 확인 및 카테고리 등록 로직
		return null;
	}

	@Transactional
	public CategoryDto updateCategory(String adminId, Long categoryId, CategoryDto categoryDto) {
		// TODO: 관리자의 권한 확인 및 카테고리 수정 처리
		return null;
	}

	@Transactional
	public void deleteCategory(String adminId, Long categoryId) {
		// TODO: 관리자의 권한 확인 및 카테고리 삭제 처리
	}

	@Transactional(readOnly = true)
	public OrderDto getOrderDetail(String adminId, Long orderId) {
		// TODO: 관리자의 권한 확인 후 주문 상세 조회
		return null;
	}

	@Transactional
	public OrderDto updateOrderStatus(String adminId, Long orderId, OrderStatusDto statusDto) {
		// TODO: 관리자의 권한 확인 및 주문 상태 업데이트
		return null;
	}

	@Transactional(readOnly = true)
	public DashboardDto getDashboard(String adminId) {
		// TODO: 관리자의 권한 확인 후 대시보드 데이터 수집 (daily_store_sales 등)
		return null;
	}

}