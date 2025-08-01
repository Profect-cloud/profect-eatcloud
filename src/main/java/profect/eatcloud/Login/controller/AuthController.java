package profect.eatcloud.Login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import profect.eatcloud.Login.dto.LoginRequestDto;
import profect.eatcloud.Login.dto.LoginResponseDto;
import profect.eatcloud.Login.dto.PasswordChangeRequestDto;
import profect.eatcloud.Login.dto.SignupRequestDto;
import profect.eatcloud.Login.service.AuthService;
import profect.eatcloud.Security.SecurityUtil;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "1. AuthController")
public class AuthController {

	private final AuthService authService;

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
}
