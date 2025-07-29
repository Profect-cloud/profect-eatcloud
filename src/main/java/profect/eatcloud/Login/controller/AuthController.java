package profect.eatcloud.Login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import profect.eatcloud.Login.dto.LoginRequestDto;
import profect.eatcloud.Login.dto.SignupDto;
import profect.eatcloud.Login.dto.TokenResponseDto;
import profect.eatcloud.Login.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto signupDto) {
        authService.signup(signupDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        String token = authService.login(loginRequestDto.getRole(), loginRequestDto.getEmail(), loginRequestDto.getPassword());
        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
