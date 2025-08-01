package profect.eatcloud.Domain.Admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import profect.eatcloud.Domain.Admin.Controller.AdminAssignController;
import profect.eatcloud.Domain.Admin.Dto.ManagerStoreApplicationDetailDto;
import profect.eatcloud.Domain.Admin.Service.AdminService;

@WebMvcTest(AdminAssignController.class)
class AdminAssignControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminService adminService;

	private static final String BASE_URL = "/api/v1/admin";

	private final UUID ADMIN_UUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private final UUID ADMIN_UUID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
	private final UUID ADMIN_UUID_3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
	private final UUID ADMIN_UUID_4 = UUID.fromString("00000000-0000-0000-0000-000000000004");

	@Test
	@WithMockUser(username = "00000000-0000-0000-0000-000000000001", roles = "ADMIN")
	void listApplications_shouldReturnOkAndInvokeService() throws Exception {
		when(adminService.getAllApplications(ADMIN_UUID_1))
			.thenReturn(Collections.emptyList());

		mockMvc.perform(get(BASE_URL + "/applies"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(adminService).getAllApplications(ADMIN_UUID_1);
	}

	@Test
	@WithMockUser(username = "00000000-0000-0000-0000-000000000002", roles = "ADMIN")
	void getDetail_shouldReturnOkAndInvokeService() throws Exception {
		UUID applicationId = UUID.randomUUID();
		ManagerStoreApplicationDetailDto detailDto = mock(ManagerStoreApplicationDetailDto.class);
		when(adminService.getApplicationDetail(ADMIN_UUID_2, applicationId))
			.thenReturn(detailDto);

		mockMvc.perform(get(BASE_URL + "/" + applicationId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(adminService).getApplicationDetail(ADMIN_UUID_2, applicationId);
	}

	@Test
	@WithMockUser(username = "00000000-0000-0000-0000-000000000003", roles = "ADMIN")
	void approve_shouldReturnNoContentAndInvokeService() throws Exception {
		UUID applicationId = UUID.randomUUID();

		mockMvc.perform(patch(BASE_URL + "/" + applicationId + "/approve"))
			.andExpect(status().isNoContent());

		verify(adminService).approveApplication(ADMIN_UUID_3, applicationId);
	}

	@Test
	@WithMockUser(username = "00000000-0000-0000-0000-000000000004", roles = "ADMIN")
	void reject_shouldReturnNoContentAndInvokeService() throws Exception {
		UUID applicationId = UUID.randomUUID();

		mockMvc.perform(patch(BASE_URL + "/" + applicationId + "/reject"))
			.andExpect(status().isNoContent());

		verify(adminService).rejectApplication(ADMIN_UUID_4, applicationId);
	}
}

