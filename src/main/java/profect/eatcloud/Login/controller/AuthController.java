package profect.eatcloud.Login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import profect.eatcloud.Login.dto.LoginRequestDto;
import profect.eatcloud.Login.dto.SignupRequestDto;
import profect.eatcloud.Login.dto.JwtResponseDto;
import profect.eatcloud.Login.service.AuthService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequestDto req) {
        String token = authService.login(req.getEmail(), req.getPassword());
        return Map.of("token", token);
    }

    // 고객 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signupCustomer(@RequestBody SignupRequestDto req) {
        authService.signup(req);
        return ResponseEntity.ok("Customer registered");
    }
}
