package profect.eatcloud.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import profect.eatcloud.login.dto.LoginRequestDto;
import profect.eatcloud.login.dto.LoginResponseDto;
import profect.eatcloud.login.dto.PasswordChangeRequestDto;
import profect.eatcloud.login.dto.SignupRequestDto;
import profect.eatcloud.login.service.AuthService;
import profect.eatcloud.login.service.RefreshTokenService;
import profect.eatcloud.security.SecurityUtil;
import profect.eatcloud.security.jwt.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "1. AuthController")
public class AuthController {

	private final AuthService authService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenService refreshTokenService;

	// 로그인
	@Operation(summary = "로그인", description = "사용자 이메일과 비밀번호를 검증하여 AccessToken과 RefreshToken, Type을 발급합니다.")
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
		LoginResponseDto response = authService.login(requestDto.getEmail(), requestDto.getPassword());
		return ResponseEntity.ok(response);
	}

	// 고객 회원가입
	@Operation(summary = "회원가입 요청", description = "이메일과 비밀번호를 받아 새로운 회원 가입을 요청합니다.")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody SignupRequestDto request) {
		authService.tempSignup(request);
		return ResponseEntity.ok("Register Success");
	}

	@Operation(summary = "이메일 인증", description = "이메일에 발송된 코드로 인증하여 회원을 등록합니다.")
	@GetMapping("/confirm-email")
	public ResponseEntity<String> confirmEmail(@RequestParam String email, @RequestParam String code) {
		authService.confirmEmail(email, code);
		return ResponseEntity.ok("회원가입이 완료되었습니다.");
	}

	@Operation(summary = "비밀번호 변경")
	@PatchMapping("/password")
	public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequestDto request) {
		String id = SecurityUtil.getCurrentUsername();
		authService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
		return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
	}

	@Operation(summary = "토큰 재발급", description = "RefreshToken을 검증하고 AccessToken과 새로운 RefreshToken을 발급합니다.")
	@PostMapping("/refresh")
	public ResponseEntity<LoginResponseDto> refreshToken(
			@RequestParam String accessToken,
			@RequestParam String refreshToken
	) {
		UUID userId = jwtTokenProvider.getIdFromToken(accessToken);
		String role = jwtTokenProvider.getTypeFromToken(accessToken);

		Object user = refreshTokenService.findUserByRoleAndId(role, userId);

		if (!refreshTokenService.isValid(user, refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String newAccessToken = jwtTokenProvider.createToken(userId, role);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, role);
		LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

		refreshTokenService.saveOrUpdateToken(user, newRefreshToken, expiryDate);

		LoginResponseDto response = LoginResponseDto.builder()
				.token(newAccessToken)
				.refreshToken(newRefreshToken)
				.type(role)
				.build();

		return ResponseEntity.ok(response);
	}
}
