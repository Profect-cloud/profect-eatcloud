package profect.eatcloud.Security.userDetails;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Entity.Admin;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;

@Service("adminUserDetailsService")
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {
	private final AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		UUID userId;
		try {
			userId = UUID.fromString(id);
		} catch (IllegalArgumentException ex) {
			throw new UsernameNotFoundException("유효하지 않은 사용자 ID 형식입니다: " + id);
		}

		Admin admin = adminRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("관리자 없음: " + id));

		return org.springframework.security.core.userdetails.User.builder()
			.username(admin.getId().toString())
			.password(admin.getPassword())
			.roles("ADMIN")
			.build();
	}
}

