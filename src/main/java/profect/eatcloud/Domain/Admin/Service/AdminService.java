package profect.eatcloud.Domain.Admin.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Dto.CategoryDto;
import profect.eatcloud.Domain.Admin.Dto.DashboardDto;
import profect.eatcloud.Domain.Admin.Dto.OrderDto;
import profect.eatcloud.Domain.Admin.Dto.OrderStatusDto;
import profect.eatcloud.Domain.Admin.Dto.StoreDto;
import profect.eatcloud.Domain.Admin.Dto.UserDto;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Order.Repository.OrderRepository_hong;
import profect.eatcloud.Domain.Store.Entity.Category;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Store.Repository.CategoryRepository_hong;
import profect.eatcloud.Domain.Store.Repository.StoreRepository_hong;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminRepository adminRepository;        // p_admins
	private final CustomerRepository customerRepository;  // p_customer
	private final StoreRepository_hong storeRepository;        // p_stores
	private final CategoryRepository_hong categoryRepository;  // p_categories
	private final OrderRepository_hong orderRepository;        // p_orders

	@Transactional(readOnly = true)
	public List<UserDto> getAllCustomers(String adminId) {
		// 1) UUID 변환
		UUID adminUuid = UUID.fromString(adminId);

		// 2) Admin 조회
		adminRepository.findById(adminUuid)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminUuid));

		// 3) Customer 전체 조회 및 DTO 매핑
		return customerRepository.findAll().stream()
			.map(c -> UserDto.builder()
				.id(c.getId())
				.name(c.getName())
				.nickname(c.getNickname())
				.email(c.getEmail())
				.phoneNumber(c.getPhoneNumber())
				.points(c.getPoints())
				.createdAt(LocalDateTime.from(c.getTimeData().getCreatedAt()))
				.build()
			)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteCustomer(String adminId, UUID userId) {
		// 1) admin 검증
		UUID adminUuid = UUID.fromString(adminId);
		adminRepository.findById(adminUuid)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminUuid));

		// 2) 삭제 대상 고객 조회 (없으면 NoSuchElementException)
		Customer customer = customerRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

		// 3) 논리삭제로
		customer.getTimeData().setDeletedAt(LocalDateTime.now());
		customer.getTimeData().setDeletedBy(String.valueOf(adminUuid));

	}

	@Transactional
	public StoreDto createStore(String adminId, StoreDto storeDto) {
		// 1) 관리자 검증
		UUID adminUuid = UUID.fromString(adminId);
		adminRepository.findById(adminUuid)
			.orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminUuid));

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

	@Transactional(readOnly = true)
	public List<StoreDto> getStores(String adminId) {
		// TODO: 관리자의 권한 확인 후 가게 목록 조회
		return null;
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