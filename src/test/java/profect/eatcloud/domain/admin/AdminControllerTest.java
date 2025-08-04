package profect.eatcloud.domain.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import profect.eatcloud.Security.jwt.JwtTokenProvider;
import profect.eatcloud.Security.userDetails.CustomUserDetailsService;
import profect.eatcloud.domain.admin.controller.AdminController;
import profect.eatcloud.domain.admin.dto.ManagerDto;
import profect.eatcloud.domain.admin.dto.StoreDto;
import profect.eatcloud.domain.admin.dto.UserDto;
import profect.eatcloud.domain.admin.message.ResponseMessage;
import profect.eatcloud.domain.admin.service.AdminService;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminService adminService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("GET /api/v1/admin/customers → 전체 고객 목록 반환 (UUID)")
	void getAllCustomers_returnsList() throws Exception {
		UUID userId = UUID.randomUUID();
		UserDto user = UserDto.builder()
			.id(userId)
			.name("홍길동")
			.nickname("길동")
			.email("hong@test.com")
			.phoneNumber("010-0000-0000")
			.points(100)
			.build();
		when(adminService.getAllCustomers()).thenReturn(List.of(user));

		mockMvc.perform(get("/api/v1/admin/customers"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].id").value(userId.toString()))
			.andExpect(jsonPath("$.data[0].name").value("홍길동"));
	}

	@Test
	@DisplayName("GET /api/v1/admin/managers → 전체 매니저 목록 반환 (UUID)")
	void getAllManagers_returnsList() throws Exception {
		UUID mgrId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		ManagerDto mgr = ManagerDto.builder()
			.id(mgrId)
			.name("관리자A")
			.email("manager@test.com")
			.phoneNumber("010-1111-1111")
			.storeId(storeId)
			.build();
		when(adminService.getAllManagers()).thenReturn(List.of(mgr));

		mockMvc.perform(get("/api/v1/admin/managers"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].id").value(mgrId.toString()))
			.andExpect(jsonPath("$.data[0].storeId").value(storeId.toString()));
	}

	@Test
	@DisplayName("GET /api/v1/admin/stores → 가게 목록 반환 (UUID)")
	void getStores_returnsList() throws Exception {
		UUID storeId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		StoreDto store = StoreDto.builder()
			.storeId(storeId)
			.storeName("테스트가게")
			.categoryId(categoryId)
			.minCost(5000)
			.description("테스트 설명")
			.storeLat(37.5665)
			.storeLon(126.9780)
			.openStatus(true)
			.openTime(LocalTime.of(9, 0))
			.closeTime(LocalTime.of(18, 0))
			.build();
		when(adminService.getStores()).thenReturn(List.of(store));

		mockMvc.perform(get("/api/v1/admin/stores"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].storeId").value(storeId.toString()))
			.andExpect(jsonPath("$.data[0].storeName").value("테스트가게"));
	}

	@Test
	@DisplayName("GET /api/v1/admin/customer/{email} → 이메일로 Customer 단건 조회 (이메일)")
	void getCustomerByEmail_returnsUser() throws Exception {
		String email = "test@example.com";
		UUID userId = UUID.randomUUID();
		UserDto user = UserDto.builder()
			.id(userId)
			.name("홍길동")
			.nickname("길동")
			.email(email)
			.phoneNumber("010-0000-0000")
			.points(100)
			.build();
		when(adminService.getCustomerByEmail(email)).thenReturn(user);

		mockMvc.perform(get("/api/v1/admin/customer/{email}", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(userId.toString()))
			.andExpect(jsonPath("$.data.email").value(email));
	}

	@Test
	@DisplayName("GET /api/v1/admin/manager/{email} → 이메일로 매니저 단건 조회 (이메일)")
	void getManagerByEmail_returnsManager() throws Exception {
		String email = "manager@test.com";
		UUID mgrId = UUID.randomUUID();
		UUID storeId2 = UUID.randomUUID();
		ManagerDto mgr = ManagerDto.builder()
			.id(mgrId)
			.name("관리자A")
			.email(email)
			.phoneNumber("010-1111-1111")
			.storeId(storeId2)
			.build();
		when(adminService.getManagerByEmail(email)).thenReturn(mgr);

		mockMvc.perform(get("/api/v1/admin/manager/{email}", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(mgrId.toString()))
			.andExpect(jsonPath("$.data.email").value(email));
	}

	@Test
	@DisplayName("GET /api/v1/admin/store/{storeId} → 가게 단건 조회 (UUID)")
	void getStore_returnsStore() throws Exception {
		UUID storeId2 = UUID.randomUUID();
		UUID categoryId2 = UUID.randomUUID();
		StoreDto store = StoreDto.builder()
			.storeId(storeId2)
			.storeName("테스트가게")
			.categoryId(categoryId2)
			.minCost(5000)
			.description("테스트 설명")
			.storeLat(37.5665)
			.storeLon(126.9780)
			.openStatus(true)
			.openTime(LocalTime.of(9, 0))
			.closeTime(LocalTime.of(18, 0))
			.build();
		when(adminService.getStore(storeId2)).thenReturn(store);

		mockMvc.perform(get("/api/v1/admin/store/{storeId}", storeId2))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.storeId").value(storeId2.toString()))
			.andExpect(jsonPath("$.data.storeName").value("테스트가게"));
	}

	// Delete operations
	@Test
	@DisplayName("DELETE /api/v1/admin/customer/{email} → 이메일로 고객 밴(논리 삭제)")
	void deleteCustomerByEmail_bansCustomer() throws Exception {
		String email = "customer@test.com";
		doNothing().when(adminService).deleteCustomerByEmail(email);

		mockMvc.perform(delete("/api/v1/admin/customer/{email}", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value(ResponseMessage.CUSTOMER_BAN_SUCCESS.getMessage()));
	}

	@Test
	@DisplayName("DELETE /api/v1/admin/manager/{email} → 이메일로 매니저 밴(논리 삭제)")
	void deleteManagerByEmail_bansManager() throws Exception {
		String email = "manager@test.com";
		doNothing().when(adminService).deleteManagerByEmail(email);

		mockMvc.perform(delete("/api/v1/admin/manager/{email}", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value(ResponseMessage.MANAGER_BAN_SUCCESS.getMessage()));
	}

	@Test
	@DisplayName("DELETE /api/v1/admin/store/{storeId} → 가게 삭제")
	void deleteStore_deletesStore() throws Exception {
		UUID storeId = UUID.randomUUID();
		doNothing().when(adminService).deleteStore(storeId);

		mockMvc.perform(delete("/api/v1/admin/store/{storeId}", storeId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value(ResponseMessage.STORE_DELETE_SUCCESS.getMessage()));
	}
}
