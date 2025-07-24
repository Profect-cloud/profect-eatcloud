package profect.eatcloud.Domain.User.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import profect.eatcloud.Domain.Global.entity.Time;
import profect.eatcloud.Domain.Global.repository.TimeRepository;
import profect.eatcloud.Domain.User.Entity.User;
import profect.eatcloud.Domain.User.Repository.UserRepository;
import profect.eatcloud.Domain.User.Dto.request.UserProfileRequestDto;
import profect.eatcloud.Domain.User.Dto.response.UserProfileResponseDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TimeRepository pTimeRepository;

    public UserProfileResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // created_at, updated_at, role은 임시로 null 처리. 추후 p_time, role_codes 조인 필요
        return UserProfileResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRoleCode())
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public UserProfileResponseDto updateUserProfile(String username, UserProfileRequestDto requestDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setNickname(requestDto.getNickname());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        userRepository.save(user);
        return UserProfileResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRoleCode())
                .createdAt(null)
                .updatedAt(java.time.Instant.now())
                .build();
    }

    public void withdrawUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UUID pTimeId = user.getPTimeId();
        Time pTime = pTimeRepository.findById(pTimeId)
                .orElseThrow(() -> new RuntimeException("PTime not found"));
        pTime.setDeletedAt(java.time.Instant.now());
        pTime.setDeletedBy(username);
        pTimeRepository.save(pTime);
    }
} 