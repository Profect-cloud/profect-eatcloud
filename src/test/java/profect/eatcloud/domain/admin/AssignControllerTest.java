package profect.eatcloud.domain.admin;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import profect.eatcloud.common.ApiResponseStatus;
import profect.eatcloud.domain.admin.controller.AssignController;
import profect.eatcloud.domain.admin.dto.ManagerStoreApplicationRequestDto;
import profect.eatcloud.domain.admin.dto.ManagerStoreApplicationResponseDto;
import profect.eatcloud.domain.admin.service.AssignService;

@WebMvcTest(AssignController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AssignControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AssignService assignService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("POST /api/v1/unauth/manager-apply → 매니저·스토어 신청하기")
	void apply_returnsCreated() throws Exception {
		// given
		UUID applicationId = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();

		ManagerStoreApplicationRequestDto request = ManagerStoreApplicationRequestDto.builder()
			.managerName("홍길동")
			.managerEmail("manager@example.com")
			.managerPassword("password123")
			.managerPhoneNumber("010-1234-5678")
			.storeName("테스트가게")
			.storeAddress("서울시 강남구")
			.storePhoneNumber("02-9876-5432")
			.categoryId(UUID.randomUUID())
			.description("신규 매니저 신청")
			.build();

		ManagerStoreApplicationResponseDto response = ManagerStoreApplicationResponseDto.builder()
			.applicationId(applicationId)
			.status("PENDING")
			.createdAt(now)
			.build();

		when(assignService.newManagerStoreApply(any(ManagerStoreApplicationRequestDto.class)))
			.thenReturn(response);

		// when / then
		mockMvc.perform(post("/api/v1/unauth/manager-apply")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value(ApiResponseStatus.CREATED.getHttpStatus().value()))
			.andExpect(jsonPath("$.message").value(ApiResponseStatus.CREATED.getDisplay()))
			.andExpect(jsonPath("$.data.applicationId").value(applicationId.toString()))
			.andExpect(jsonPath("$.data.status").value("PENDING"))
			.andExpect(jsonPath("$.data.createdAt").exists());
	}
}
