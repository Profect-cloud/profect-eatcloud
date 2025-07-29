package profect.eatcloud.Domain.Admin;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

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
import profect.eatcloud.Domain.Admin.Dto.UserDto;
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

	@DisplayName("관리자 전체 사용자 목록 조회 성공")
	@Test
	void givenUsers_whenGetAllUsers_thenReturnUserList() throws Exception {
		// given
		String adminId = "550e8400-e29b-41d4-a716-446655440000";
		UserDto userDto = UserDto.builder()
			.id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
			.name("홍길동")
			.nickname("gildong")
			.email("gildong@example.com")
			.phoneNumber("010-1234-5678")
			.points(1000)
			.createdAt(LocalDateTime.of(2025, 7, 29, 15, 0))
			.build();

		given(adminService.getAllCustomers(adminId))
			.willReturn(Collections.singletonList(userDto));

		// when & then
		mockMvc.perform(get("/users")
				.with(user(adminId))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(userDto.getId().toString()))
			.andExpect(jsonPath("$[0].name").value(userDto.getName()))
			.andExpect(jsonPath("$[0].nickname").value(userDto.getNickname()))
			.andExpect(jsonPath("$[0].email").value(userDto.getEmail()))
			.andExpect(jsonPath("$[0].phoneNumber").value(userDto.getPhoneNumber()))
			.andExpect(jsonPath("$[0].points").value(userDto.getPoints()))
			.andExpect(jsonPath("$[0].createdAt").value(userDto.getCreatedAt().toString()));

		then(adminService).should().getAllCustomers(adminId);
	}
}
