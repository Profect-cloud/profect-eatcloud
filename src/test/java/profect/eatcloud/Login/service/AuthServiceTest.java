package profect.eatcloud.Login.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;
import profect.eatcloud.Login.dto.SignupRequestDto;
import profect.eatcloud.Security.jwt.JwtTokenProvider;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtTokenProvider    tokenProvider;
    @Mock CustomerRepository  customerRepo;
    @Mock BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입: 신규 고객이면 save() 호출")
    void registerCustomer_success() {
        // given
        SignupRequestDto req = new SignupRequestDto();
        req.setEmail("new@customer.com");
        req.setPassword("rawPass");
        req.setName("홍길동");
        req.setNickname("gildong");

        given(customerRepo.findByEmail(req.getEmail()))
                .willReturn(Optional.empty());
        given(passwordEncoder.encode("rawPass"))
                .willReturn("ENC(rawPass)");

        // when
        authService.signup(req);

        // then: save() 시 들어간 Customer의 필드 검증
        then(customerRepo).should().save(argThat((Customer c) ->
                c.getEmail().equals("new@customer.com") &&
                        c.getName().equals("홍길동") &&
                        c.getNickname().equals("gildong") &&
                        c.getPassword().equals("ENC(rawPass)")
        ));
    }

    @Test
    @DisplayName("회원가입: 중복 이메일이면 예외 발생")
    void registerCustomer_duplicateEmail_throws() {
        // given
        SignupRequestDto req = new SignupRequestDto();
        req.setEmail("new@customer.com");
        req.setPassword("rawPass");
        req.setName("홍길동");
        req.setNickname("gildong");
        given(customerRepo.findByEmail(req.getEmail()))
                .willReturn(Optional.of(new Customer()));

        // when / then
        assertThatThrownBy(() -> authService.signup(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 존재하는 이메일");
        then(customerRepo).should(never()).save(any());
    }

    @Test
    @DisplayName("로그인: 관리자로 로그인하면 ROLE_ADMIN 포함 토큰 생성")
    void login_asAdmin_createsTokenWithAdminRole() {
        // given
        String email = "admin@domain.com";
        String pwd   = "adminPass!";
        String fakeToken = "FAKE.JWT.TOKEN";

        // AuthenticationManager.authenticate() 가 리턴할 mock Authentication
        var authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                /* principal=email */ email,
                /* credentials */ null,
                /* authorities  */ List.of(authority)
        );

        given(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, pwd)))
                .willReturn(mockAuth);

        given(tokenProvider.createToken(
                eq(email), anyList()))
                .willReturn(fakeToken);

        // when
        String token = authService.login(email, pwd);

        // then
        assertThat(token).isEqualTo(fakeToken);
        then(tokenProvider).should().createToken(
                eq(email),
                argThat(list -> list.contains("ROLE_ADMIN"))
        );
    }

    @Test
    @DisplayName("로그인: 고객으로 로그인하면 ROLE_CUSTOMER 포함 토큰 생성")
    void login_asCustomer_createsTokenWithCustomerRole() {
        // given
        String email = "user@customer.com";
        String pwd   = "userPass";
        String fakeToken = "FAKE.CUS.JWT";

        var authz = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                email, null, List.of(authz)
        );

        given(authenticationManager.authenticate(any()))
                .willReturn(mockAuth);
        given(tokenProvider.createToken(eq(email), anyList()))
                .willReturn(fakeToken);

        // when
        String token = authService.login(email, pwd);

        // then
        assertThat(token).isEqualTo(fakeToken);
        then(tokenProvider).should().createToken(
                eq(email),
                argThat(list -> list.contains("ROLE_CUSTOMER"))
        );
    }
}