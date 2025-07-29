package profect.eatcloud.Domain.Admin;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import profect.eatcloud.Domain.Admin.Controller.AdminController;
import profect.eatcloud.Domain.Admin.Dto.CustomerDto;
import profect.eatcloud.Domain.Admin.Service.AdminService;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

	private MockMvc mockMvc;

	@Mock
	private AdminService adminService;

	@InjectMocks
	private AdminController adminController;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(adminController)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();
	}

	@Test
	@DisplayName("GET /admin/users - 전체 사용자 목록 조회 성공")
	void testGetAllCustomers() throws Exception {
		CustomerDto dto = CustomerDto.builder().id(null).name("John").build();
		given(adminService.getAllCustomers("admin-uuid"))
			.willReturn(Collections.singletonList(dto));

		mockMvc.perform(get("/admin/users")
				.with(user("admin-uuid"))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].name").value("John"));
	}

	@Test
	@DisplayName("DELETE /admin/users/{id} - 사용자 삭제 성공")
	void testDeleteCustomer() throws Exception {
		Long customerId = 1L;
		doNothing().when(adminService).deleteCustomer("admin-uuid", customerId);

		mockMvc.perform(delete("/admin/users/{id}", customerId)
				.with(user("admin-uuid")))
			.andExpect(status().isOk())
			.andExpect(content().string("삭제 완료"));
	}

	// TODO: 기타 엔드포인트 테스트 추가
}
