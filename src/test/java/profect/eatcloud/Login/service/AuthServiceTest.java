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


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

    @Test
    void signup_WhenValidRequest_SavesCustomerSuccessfully() {
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
    void signup_WhenEmailAlreadyExists_ThrowsException() {
        // given
        SignupRequestDto req = new SignupRequestDto("test@example.com", "Password1!", "홍길동", "길동이", "010-1234-1234");
        given(customerRepository.findByEmail(req.getEmail())).willReturn(Optional.of(mock(Customer.class)));

        // when & then
        assertThatThrownBy(() -> authService.signup(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 존재하는 이메일");
    }

    @Test
    void login_Admin_ReturnsToken() {
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

    @Test
    void login_Manager_ReturnsToken() {
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
    void login_Customer_ReturnsToken() {
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
        given(jwtTokenProvider.createToken(id, "customer")).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(id, "customer")).willReturn("refresh-token");

        LoginResponseDto res = authService.login(email, rawPassword);

        assertThat(res.getToken()).isEqualTo("access-token");
        assertThat(res.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(res.getType()).isEqualTo("customer");
    }

    @Test
    void login_WhenPasswordInvalid_ThrowsException() {
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
    void login_WhenEmailNotFound_ThrowsException() {
        String email = "nouser@test.com";

        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());
        given(managerRepository.findByEmail(email)).willReturn(Optional.empty());
        given(customerRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(email, "pass"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void changePassword_Admin_Success() {
        String email = "admin@example.com";
        String currentPassword = "oldPass";
        String newPassword = "newPass";

        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword("encodedOldPass");

        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(passwordEncoder.matches(currentPassword, admin.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPass");
        given(adminRepository.save(any(Admin.class))).willReturn(admin);

        authService.changePassword(email, currentPassword, newPassword);

        verify(adminRepository).save(argThat(savedAdmin ->
                savedAdmin.getPassword().equals("encodedNewPass")
        ));
    }

    @Test
    void changePassword_Customer_Success() {
        String email = "customer@example.com";
        String currentPassword = "oldPass";
        String newPassword = "newPass";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword("encodedOldPass");

        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());
        given(managerRepository.findByEmail(email)).willReturn(Optional.empty());
        given(customerRepository.findByEmail(email)).willReturn(Optional.of(customer));
        given(passwordEncoder.matches(currentPassword, customer.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPass");
        given(customerRepository.save(any(Customer.class))).willReturn(customer);

        authService.changePassword(email, currentPassword, newPassword);

        verify(customerRepository).save(argThat(savedCustomer ->
                savedCustomer.getPassword().equals("encodedNewPass")
        ));
    }

    @Test
    void changePassword_UserNotFound_Throws() {
        String email = "unknown@example.com";
        String currentPassword = "oldPass";
        String newPassword = "newPass";

        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());
        given(managerRepository.findByEmail(email)).willReturn(Optional.empty());
        given(customerRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authService.changePassword(email, currentPassword, newPassword));
    }

    @Test
    void changePassword_WrongCurrentPassword_Throws() {
        String email = "customer@example.com";
        String currentPassword = "wrongOldPass";
        String newPassword = "newPass";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword("encodedOldPass");

        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());
        given(managerRepository.findByEmail(email)).willReturn(Optional.empty());
        given(customerRepository.findByEmail(email)).willReturn(Optional.of(customer));
        given(passwordEncoder.matches(currentPassword, customer.getPassword())).willReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> authService.changePassword(email, currentPassword, newPassword));

        verify(customerRepository, never()).save(any());
    }
}

