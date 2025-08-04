package profect.eatcloud.domain.admin;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import profect.eatcloud.Security.jwt.JwtTokenProvider;
import profect.eatcloud.Security.userDetails.CustomUserDetailsService;
import profect.eatcloud.domain.admin.controller.AdminAssignController;
import profect.eatcloud.domain.admin.dto.ManagerStoreApplicationDetailDto;
import profect.eatcloud.domain.admin.dto.ManagerStoreApplicationSummaryDto;
import profect.eatcloud.domain.admin.message.ResponseMessage;
import profect.eatcloud.domain.admin.service.AdminAssignService;

@WebMvcTest(AdminAssignController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AdminAssignControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminAssignService adminService;

	// Security 관련 빈들은 MockitoBean으로 등록만 해두면 됩니다
	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;
	@MockitoBean
	private CustomUserDetailsService customUserDetailsService;

	@Test
	@DisplayName("GET /api/v1/admin/applies → 신청서 목록 조회")
	void listApplications_returnsList() throws Exception {
		UUID appId = UUID.randomUUID();
		ManagerStoreApplicationSummaryDto dto = ManagerStoreApplicationSummaryDto.builder()
			.applicationId(appId)
			.managerName("김관리자")
			.managerEmail("manager@test.com")
			.storeName("테스트가게")
			.status("PENDING")
			.appliedAt(LocalDateTime.now())
			.build();
		when(adminService.getAllApplications()).thenReturn(List.of(dto));

		mockMvc.perform(get("/api/v1/admin/applies"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].applicationId").value(appId.toString()))
			.andExpect(jsonPath("$.data[0].managerEmail").value("manager@test.com"));
	}

	@Test
	@DisplayName("GET /api/v1/admin/apply/{id} → 신청서 상세 조회")
	void getDetail_returnsDetail() throws Exception {
		UUID appId = UUID.randomUUID();
		ManagerStoreApplicationDetailDto dto = ManagerStoreApplicationDetailDto.builder()
			.applicationId(appId)
			.managerName("김관리자")
			.managerEmail("manager@test.com")
			.managerPhoneNumber("010-1234-5678")
			.storeName("테스트가게")
			.storeAddress("서울시")
			.storePhoneNumber("02-123-4567")
			.categoryId(null)
			.description("설명")
			.status("PENDING")
			.reviewerAdminId(null)
			.reviewComment(null)
			.appliedAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();
		when(adminService.getApplicationDetail(appId)).thenReturn(dto);

		mockMvc.perform(get("/api/v1/admin/apply/{id}", appId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.applicationId").value(appId.toString()))
			.andExpect(jsonPath("$.data.managerName").value("김관리자"));
	}

	@Test
	@DisplayName("PATCH /api/v1/admin/apply/{id}/approve → 신청서 승인")
	void approveApplication_success() throws Exception {
		UUID adminId = UUID.randomUUID();
		UUID appId = UUID.randomUUID();

		// 스프링 시큐리티 UserDetails 생성
		UserDetails userDetails = User.withUsername(adminId.toString())
			.password("dummy")
			.roles("ADMIN")
			.build();

		// 서비스 메서드 목 설정
		doNothing().when(adminService).approveApplication(adminId, appId);

		mockMvc.perform(patch("/api/v1/admin/apply/{id}/approve", appId)
				// SecurityContext 에 userDetails 를 넣어줍니다
				.with(user(userDetails))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data")
				.value(ResponseMessage.APPLICATION_APPROVE_SUCCESS.getMessage()));
	}

	@Test
	@DisplayName("PATCH /api/v1/admin/apply/{id}/reject → 신청서 거절")
	void rejectApplication_success() throws Exception {
		UUID adminId = UUID.randomUUID();
		UUID appId = UUID.randomUUID();

		UserDetails userDetails = User.withUsername(adminId.toString())
			.password("dummy")
			.roles("ADMIN")
			.build();

		doNothing().when(adminService).rejectApplication(adminId, appId);

		mockMvc.perform(patch("/api/v1/admin/apply/{id}/reject", appId)
				.with(user(userDetails))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data")
				.value(ResponseMessage.APPLICATION_REJECT_SUCCESS.getMessage()));
	}
}
