package profect.eatcloud.Domain.Admin.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import profect.eatcloud.Domain.Admin.Dto.ManagerDto;
import profect.eatcloud.Domain.Admin.Dto.UserDto;
import profect.eatcloud.Domain.Admin.Entity.Admin;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Entity.Manager;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@InjectMocks
	AdminService adminService;
	@Mock
	AdminRepository adminRepository;
	@Mock
	CustomerRepository customerRepository;
	@Mock
	ManagerRepository managerRepository;

	@Test
	void getAllCustomers_shouldThrowWhenAdminNotFound() {
		// given
		UUID fakeAdminId = UUID.randomUUID();
		when(adminRepository.findById(fakeAdminId))
			.thenReturn(java.util.Optional.empty());

		// when & then
		assertThrows(NoSuchElementException.class,
			() -> adminService.getAllCustomers(fakeAdminId));
	}

	@Test
	void getAllCustomers_shouldReturnMappedDtoList() {
		// given
		UUID adminId = UUID.randomUUID();
		// Admin 검증용 더미 리포지토리 응답
		when(adminRepository.findById(adminId))
			.thenReturn(Optional.of(
				profect.eatcloud.Domain.Admin.Entity.Admin.builder()
					.id(adminId)
					.email("x@x.com")
					.password("p")
					.build()
			));

		// 5개의 고객 엔티티 더미 생성
		List<Customer> customers = IntStream.rangeClosed(1, 5)
			.mapToObj(i -> Customer.builder()
				.id(UUID.randomUUID())
				.name("사용자" + i)
				.nickname("닉네임" + i)
				.email("user" + i + "@example.com")
				.phoneNumber("010-0000-000" + i)
				.points(100 * i)
				.build()
			)
			.collect(Collectors.toList());
		when(customerRepository.findAll())
			.thenReturn(customers);

		// when
		List<UserDto> dtos = adminService.getAllCustomers(adminId);

		// then
		assertEquals(5, dtos.size(), "고객이 5명 반환되어야 한다");

		// 몇 개 항목만 랜덤으로 샘플 체크
		for (int idx : List.of(0, 2, 4)) {
			Customer c = customers.get(idx);
			UserDto dto = dtos.get(idx);

			assertEquals(c.getId(), dto.getId());
			assertEquals(c.getName(), dto.getName());
			assertEquals(c.getNickname(), dto.getNickname());
			assertEquals(c.getEmail(), dto.getEmail());
			assertEquals(c.getPhoneNumber(), dto.getPhoneNumber());
			assertEquals(c.getPoints(), dto.getPoints());
		}
	}

	@Test
	void getAllManagers_shouldReturnMappedDtoList() {
		// given
		UUID adminId = UUID.randomUUID();
		when(adminRepository.findById(adminId))
			.thenReturn(Optional.of(
				Admin.builder()
					.id(adminId)
					.email("admin@example.com")
					.password("pass")
					.build()
			));

		Manager m1 = Manager.builder()
			.id(UUID.randomUUID())
			.name("mgr1")
			.email("mgr1@example.com")
			.phoneNumber("010-1111-1111")
			.build();
		Manager m2 = Manager.builder()
			.id(UUID.randomUUID())
			.name("mgr2")
			.email("mgr2@example.com")
			.phoneNumber("010-2222-2222")
			.build();
		when(managerRepository.findAll())
			.thenReturn(List.of(m1, m2));

		// when
		List<ManagerDto> dtos = adminService.getAllManagers(adminId);

		// then
		assertEquals(2, dtos.size());
		assertEquals(m1.getId(), dtos.get(0).getId());
		assertEquals("mgr1", dtos.get(0).getName());
		assertEquals("mgr1@example.com", dtos.get(0).getEmail());
		assertEquals("010-1111-1111", dtos.get(0).getPhoneNumber());

		assertEquals(m2.getId(), dtos.get(1).getId());
		assertEquals("mgr2", dtos.get(1).getName());
		assertEquals("mgr2@example.com", dtos.get(1).getEmail());
		assertEquals("010-2222-2222", dtos.get(1).getPhoneNumber());
	}

	// --- Customer by Email ---

	@Test
	void getCustomerByEmail_shouldThrowWhenAdminNotFound() {
		String email = "noone@example.com";
		String fakeAdminId = UUID.randomUUID().toString();
		given(adminRepository.findById(UUID.fromString(fakeAdminId)))
			.willReturn(Optional.empty());

		assertThrows(NoSuchElementException.class,
			() -> adminService.getCustomerByEmail(UUID.fromString(fakeAdminId), email));
	}

	@Test
	void getCustomerByEmail_shouldThrowWhenCustomerNotFound() {
		UUID adminId = UUID.randomUUID();
		String email = "absent@example.com";
		given(adminRepository.findById(adminId))
			.willReturn(Optional.of(Admin.builder()
				.id(adminId)
				.email("x@x.com")
				.password("p").build()));
		given(customerRepository.findByEmail(email))
			.willReturn(Optional.empty());

		assertThrows(NoSuchElementException.class,
			() -> adminService.getCustomerByEmail(adminId, email));
	}

	@Test
	void getCustomerByEmail_shouldReturnMappedDto() {
		UUID adminId = UUID.randomUUID();
		String email = "hong@example.com";
		UUID custId = UUID.randomUUID();

		// admin 검증
		given(adminRepository.findById(adminId))
			.willReturn(Optional.of(Admin.builder()
				.id(adminId).email("a@a.com").password("p").build()));
		// customer 조회
		Customer c = Customer.builder()
			.id(custId)
			.name("홍길동")
			.nickname("어피치")
			.email(email)
			.phoneNumber("010-1111-2222")
			.points(500)
			.build();
		given(customerRepository.findByEmail(email))
			.willReturn(Optional.of(c));

		UserDto dto = adminService.getCustomerByEmail(adminId, email);

		assertEquals(custId, dto.getId());
		assertEquals("홍길동", dto.getName());
		assertEquals("어피치", dto.getNickname());
		assertEquals(email, dto.getEmail());
		assertEquals("010-1111-2222", dto.getPhoneNumber());
		assertEquals(500, dto.getPoints());
	}

	// --- Manager by Email ---

	@Test
	void getManagerByEmail_shouldThrowWhenAdminNotFound() {
		String email = "mgr@example.com";
		String fakeAdminId = UUID.randomUUID().toString();
		given(adminRepository.findById(UUID.fromString(fakeAdminId)))
			.willReturn(Optional.empty());

		assertThrows(NoSuchElementException.class,
			() -> adminService.getManagerByEmail(UUID.fromString(fakeAdminId), email));
	}

	@Test
	void getManagerByEmail_shouldThrowWhenManagerNotFound() {
		UUID adminId = UUID.randomUUID();
		String email = "none@manager.com";
		given(adminRepository.findById(adminId))
			.willReturn(Optional.of(Admin.builder()
				.id(adminId).email("a@a.com").password("p").build()));
		given(managerRepository.findByEmail(email))
			.willReturn(Optional.empty());

		assertThrows(NoSuchElementException.class,
			() -> adminService.getManagerByEmail(adminId, email));
	}

	@Test
	void getManagerByEmail_shouldReturnMappedDto() {
		UUID adminId = UUID.randomUUID();
		String email = "mgr@example.com";
		UUID mgrId = UUID.randomUUID();

		given(adminRepository.findById(adminId))
			.willReturn(Optional.of(Admin.builder()
				.id(adminId).email("a@a.com").password("p").build()));
		Manager m = Manager.builder()
			.id(mgrId)
			.name("박사장")
			.email(email)
			.phoneNumber("010-2222-3333")
			.build();
		given(managerRepository.findByEmail(email))
			.willReturn(Optional.of(m));

		ManagerDto dto = adminService.getManagerByEmail(adminId, email);

		assertEquals(mgrId, dto.getId());
		assertEquals("박사장", dto.getName());
		assertEquals(email, dto.getEmail());
		assertEquals("010-2222-3333", dto.getPhoneNumber());
	}


	/*
	@Test
	void createManager_shouldThrowWhenAdminNotFound() {
		UUID fakeAdmin = UUID.randomUUID();
		ManagerDto dto = ManagerDto.builder()
			.name("u").password("p")
			.email("e@e.com").phoneNumber("010").position("pos")
			.storeId(UUID.randomUUID())
			.build();

		given(adminRepository.findById(fakeAdmin)).willReturn(Optional.empty());

		assertThrows(NoSuchElementException.class,
			() -> adminService.createManagerAndAssignStore(fakeAdmin, dto));
	}

	@Test
	void createManager_shouldThrowWhenStoreNotFound() {
		UUID adminId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		ManagerDto dto = ManagerDto.builder()
			.username("u").password("p")
			.email("e@e.com").phoneNumber("010").position("pos")
			.storeId(storeId)
			.build();

		given(adminRepository.findById(adminId))
			.willReturn(Optional.of(Admin.builder().id(adminId).email("a@a").password("p").build()));
		given(storeRepository.findById(storeId)).willReturn(Optional.empty());

		assertThrows(NoSuchElementException.class,
			() -> adminService.createManagerAndAssignStore(adminId, dto));
	}

	@Test
	void createManager_shouldSaveManagerAndAssignStore() {
		// given
		UUID adminId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		ManagerDto dto = ManagerDto.builder()
			.username("mgr1")
			.password("rawpw")
			.email("mgr@example.com")
			.phoneNumber("010-1234-5678")
			.position("사장님")
			.storeId(storeId)
			.build();

		Store store = Store.builder().storeId(storeId).build();
		given(adminRepository.findById(adminId))
			.willReturn(Optional.of(Admin.builder().id(adminId).email("a@a").password("p").build()));
		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
		given(passwordEncoder.encode("rawpw")).willReturn("encpw");

		Manager savedMgr = Manager.builder()
			.id(UUID.randomUUID())
			.name("mgr1")
			.email("mgr@example.com")
			.phoneNumber("010-1234-5678")
			.position("사장님")
			.build();
		given(managerRepository.save(any(Manager.class))).willReturn(savedMgr);

		// when
		ManagerDto result = adminService.createManagerAndAssignStore(adminId, dto);

		// then
		then(managerRepository).should().save(argThat(m ->
			m.getUsername().equals("mgr1") &&
				m.getPassword().equals("encpw") &&
				m.getEmail().equals("mgr@example.com")
		));
		then(storeRepository).should().save(store);
		assertEquals(savedMgr.getId(), result.getId());
		assertEquals("mgr1", result.getName());
		assertEquals("mgr@example.com", result.getEmail());
		assertEquals("010-1234-5678", result.getPhoneNumber());
		assertEquals("사장님", result.getPosition());
		assertEquals(storeId, result.getStoreId());
	}
	 */

	@Test
	void createStore() {
	}

	@Test
	void getStores() {
	}

	@Test
	void updateStore() {
	}

	@Test
	void deleteStore() {
	}

	@Test
	void createCategory() {
	}

	@Test
	void updateCategory() {
	}

	@Test
	void deleteCategory() {
	}

	@Test
	void getOrderDetail() {
	}

	@Test
	void updateOrderStatus() {
	}

	@Test
	void getDashboard() {
	}
}