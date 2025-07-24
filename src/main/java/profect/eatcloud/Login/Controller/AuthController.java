package profect.eatcloud.Login.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Login.Dto.*;
import profect.eatcloud.Login.Dto.*;
import profect.eatcloud.Login.Service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {
        return ResponseEntity.ok(authService.signup(dto));
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDto> signin(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.signin(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃 (미구현)");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me() {
        return ResponseEntity.ok(authService.getMe());
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.checkEmail(email));
    }

    @PostMapping("/email")
    public ResponseEntity<String> emailAuth(@RequestBody EmailAuthRequestDto dto) {
        return ResponseEntity.ok(authService.emailAuth(dto));
    }
} 