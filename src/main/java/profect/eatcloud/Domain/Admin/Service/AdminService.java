package profect.eatcloud.Domain.Admin.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Dto.CategoryDto;
import profect.eatcloud.Domain.Admin.Dto.CustomerDto;
import profect.eatcloud.Domain.Admin.Dto.DashboardDto;
import profect.eatcloud.Domain.Admin.Dto.OrderDto;
import profect.eatcloud.Domain.Admin.Dto.OrderStatusDto;
import profect.eatcloud.Domain.Admin.Dto.StoreDto;
import profect.eatcloud.Domain.Admin.Dto.UserDto;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Order.Repository.OrderRepository_hong;
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

	public List<UserDto> getAllUsers(String adminId) {
		return null;
	}

	public void deleteUser(String adminId, Long userId) {
		return;
	}

	@Transactional(readOnly = true)
	public List<CustomerDto> getAllCustomers(String adminId) {
		// TODO: 관리자의 권한 확인 후 고객 목록 조회
		return null;
	}

	@Transactional
	public void deleteCustomer(String adminId, Long customerId) {
		// TODO: 관리자의 권한 확인 후 고객 삭제 처리
	}

	@Transactional
	public StoreDto createStore(String adminId, StoreDto storeDto) {
		// TODO: 관리자의 권한 확인 및 가게 등록 로직
		return null;
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