package profect.eatcloud.Domain.User.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import profect.eatcloud.Domain.User.Dto.request.UserProfileRequestDto;
import profect.eatcloud.Domain.User.Dto.response.UserProfileResponseDto;
import profect.eatcloud.Domain.User.Service.UserService;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            Authentication authentication,
            @RequestBody UserProfileRequestDto requestDto) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateUserProfile(username, requestDto));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> withdrawUser(Authentication authentication) {
        String username = authentication.getName();
        userService.withdrawUser(username);
        return ResponseEntity.noContent().build();
    }
} 