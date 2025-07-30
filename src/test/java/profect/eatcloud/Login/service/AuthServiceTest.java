package profect.eatcloud.Login.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import profect.eatcloud.Domain.Admin.Entity.Admin;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Entity.Manager;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;
import profect.eatcloud.Login.dto.LoginResponseDto;
import profect.eatcloud.Login.dto.SignupRequestDto;
import profect.eatcloud.Security.jwt.JwtTokenProvider;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private CustomerRepository customerRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private ManagerRepository managerRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks private AuthService authService;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    // 회원가입 테스트
    @Test
    void 회원가입_정상처리() {
        // given
        SignupRequestDto req = new SignupRequestDto("test@example.com", "Password1!", "홍길동", "길동이", "010-1234-1234");

        given(customerRepository.findByEmail(req.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(req.getPassword())).willReturn("encodedPassword");

        // when
        authService.signup(req);

        // then
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();

        assertThat(savedCustomer.getEmail()).isEqualTo(req.getEmail());
        assertThat(savedCustomer.getPassword()).isEqualTo("encodedPassword"); // encode()가 리턴한 값과 비교
        assertThat(savedCustomer.getName()).isEqualTo(req.getName());
        assertThat(savedCustomer.getNickname()).isEqualTo(req.getNickname());
    }

    @Test
    void 회원가입_중복이메일_예외() {
        // given
        SignupRequestDto req = new SignupRequestDto("test@example.com", "Password1!", "홍길동", "길동이", "010-1234-1234");
        given(customerRepository.findByEmail(req.getEmail())).willReturn(Optional.of(mock(Customer.class)));

        // when & then
        assertThatThrownBy(() -> authService.signup(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 존재하는 이메일");
    }

    // 로그인 테스트: Admin
    @Test
    void 로그인_Admin_성공() {
        String email = "admin@test.com";
        String rawPassword = "adminPass";
        Admin admin = mock(Admin.class);
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(admin.getPassword()).willReturn("encoded");
        given(admin.getId()).willReturn(id);
        given(passwordEncoder.matches(rawPassword, "encoded")).willReturn(true);
        given(jwtTokenProvider.createToken(id, "admin")).willReturn("access-token");

        LoginResponseDto res = authService.login(email, rawPassword);

        assertThat(res.getToken()).isEqualTo("access-token");
        assertThat(res.getRefreshToken()).isNull();
        assertThat(res.getType()).isEqualTo("admin");
    }

    // 로그인 테스트: Manager
    @Test
    void 로그인_Manager_성공() {
        String email = "manager@test.com";
        String rawPassword = "managerPass";
        Manager manager = mock(Manager.class);
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());
        given(managerRepository.findByEmail(email)).willReturn(Optional.of(manager));
        given(manager.getPassword()).willReturn("encoded");
        given(manager.getId()).willReturn(id);
        given(passwordEncoder.matches(rawPassword, "encoded")).willReturn(true);
        given(jwtTokenProvider.createToken(id, "manager")).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(id, "manager")).willReturn("refresh-token");

        LoginResponseDto res = authService.login(email, rawPassword);

        assertThat(res.getToken()).isEqualTo("access-token");
        assertThat(res.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(res.getType()).isEqualTo("manager");
    }

    // 로그인 테스트: Customer
    @Test
    void 로그인_Customer_성공() {
        String email = "user@test.com";
        String rawPassword = "userPass";
        Customer customer = mock(Customer.class);
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());
        given(managerRepository.findByEmail(email)).willReturn(Optional.empty());
        given(customerRepository.findByEmail(email)).willReturn(Optional.of(customer));
        given(customer.getPassword()).willReturn("encoded");
        given(customer.getId()).willReturn(id);
        given(passwordEncoder.matches(rawPassword, "encoded")).willReturn(true);
        given(jwtTokenProvider.createToken(id, "user")).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(id, "user")).willReturn("refresh-token");

        LoginResponseDto res = authService.login(email, rawPassword);

        assertThat(res.getToken()).isEqualTo("access-token");
        assertThat(res.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(res.getType()).isEqualTo("user");
    }

    @Test
    void 로그인_비밀번호_틀릴경우_예외() {
        String email = "admin@test.com";
        Admin admin = mock(Admin.class);

        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(admin.getPassword()).willReturn("encoded");
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        assertThatThrownBy(() -> authService.login(email, "wrongPassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호");
    }

    @Test
    void 로그인_없는_이메일이면_예외() {
        String email = "nouser@test.com";

        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());
        given(managerRepository.findByEmail(email)).willReturn(Optional.empty());
        given(customerRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(email, "pass"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}