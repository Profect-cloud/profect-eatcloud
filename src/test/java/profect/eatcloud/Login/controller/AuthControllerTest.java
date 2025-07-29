package profect.eatcloud.Login.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import profect.eatcloud.Login.dto.LoginRequestDto;
import profect.eatcloud.Login.dto.SignupRequestDto;
import profect.eatcloud.Login.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    @DisplayName("POST /auth/signup → 200, Customer registered")
    void signup() throws Exception {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail("a@b.com");
        dto.setPassword("pwd");
        dto.setName("홍길동");
        dto.setNickname("nick");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer registered"));

        then(authService).should().signup(any(SignupRequestDto.class));
    }

    @Test
    @DisplayName("POST /auth/login → 200, token JSON 반환")
    void login() throws Exception {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("admin@domain.com");
        dto.setPassword("adminPass!");

        // AuthService 가 반환할 fake token
        String fakeToken = "eyFAKE.TOKEN";
        given(authService.login(eq("admin@domain.com"), eq("adminPass!")))
                .willReturn(fakeToken);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeToken));

        then(authService).should().login(dto.getEmail(), dto.getPassword());
    }
}