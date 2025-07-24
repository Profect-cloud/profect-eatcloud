package profect.eatcloud.Login.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Login.Dto.*;
import profect.eatcloud.Login.Dto.*;
import profect.eatcloud.Login.Repository.UserRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public String signup(SignupRequestDto dto) {
        // 실제 구현 시 비밀번호 암호화, 중복 체크 등 처리
        return "회원가입 완료 (미구현)";
    }

    public LoginResponseDto signin(LoginRequestDto dto) {
        // 실제 구현 시 비밀번호 검증, 토큰 발급 등 처리
        return LoginResponseDto.builder()
                .accessToken("dummy-access-token")
                .refreshToken("dummy-refresh-token")
                .userId(UUID.randomUUID().toString())
                .username(dto.getUsername())
                .email("dummy@email.com")
                .nickname("dummy-nickname")
                .roleCode("USER")
                .build();
    }

    public UserResponseDto getMe() {
        // 실제 구현 시 인증된 사용자 정보 반환
        return UserResponseDto.builder()
                .id(UUID.randomUUID())
                .username("dummy")
                .nickname("dummy-nickname")
                .email("dummy@email.com")
                .roleCode("USER")
                .phoneNumber("010-0000-0000")
                .pTimeId(UUID.randomUUID())
                .build();
    }

    public Boolean checkEmail(String email) {
        // 실제 구현 시 이메일 중복 체크
        return false;
    }

    public String emailAuth(EmailAuthRequestDto dto) {
        // 실제 구현 시 이메일 인증 처리
        return "이메일 인증 (미구현)";
    }
} 