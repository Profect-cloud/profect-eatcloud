package profect.eatcloud.Login.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import profect.eatcloud.Login.dto.LoginResponseDto;
import profect.eatcloud.Login.dto.SignupRequestDto;
import profect.eatcloud.Login.service.AuthService;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        Authentication auth = new UsernamePasswordAuthenticationToken("user@example.com", null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_Success() throws Exception {
        String email = "test@example.com";
        String password = "Password123!";

        LoginResponseDto responseDto = new LoginResponseDto("access-token", "refresh-token", "user");

        given(authService.login(email, password)).willReturn(responseDto);

        String json = """
            {
                "email": "test@example.com",
                "password": "Password123!"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.type").value("user"));

        verify(authService).login(email, password);
    }

    @Test
    void register_Success() throws Exception {
        String json = """
            {
                "email": "test@example.com",
                "password": "Password123!",
                "name": "홍길동",
                "nickname": "길동이",
                "phone": "010-1234-5678"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Register Success"));

        verify(authService).signup(any(SignupRequestDto.class));
    }

    @Test
    public void changePassword_Success() throws Exception {
        String json = "{ \"currentPassword\": \"oldPass123!\", \"newPassword\": \"newPass456!\" }";

        Principal principal = () -> "user@example.com";

        doNothing().when(authService).changePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/v1/auth/password")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(authService, times(1)).changePassword("user@example.com", "oldPass123!", "newPass456!");
    }
}
