package profect.eatcloud.Login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import profect.eatcloud.Login.dto.LoginRequestDto;
import profect.eatcloud.Login.dto.LoginResponseDto;
import profect.eatcloud.Login.dto.SignupRequestDto;
import profect.eatcloud.Login.service.AuthService;

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
	@Operation(summary = "회원가입", description = "이메일과 비밀번호를 받아 새로운 회원을 등록합니다.")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody SignupRequestDto request) {
		authService.signup(request);
		return ResponseEntity.ok("Register Success");
	}
}
