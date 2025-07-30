package profect.eatcloud.Security.userDetails;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Manager.Entity.Manager;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;

@Service("managerUserDetailsService")
@RequiredArgsConstructor
public class ManagerUserDetailsService implements UserDetailsService {
	private final ManagerRepository managerRepository;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		UUID userId;
		try {
			userId = UUID.fromString(id);
		} catch (IllegalArgumentException ex) {
			throw new UsernameNotFoundException("유효하지 않은 사용자 ID 형식입니다: " + id);
		}

		Manager mgr = managerRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("매니저 없음: " + id));

		return org.springframework.security.core.userdetails.User.builder()
			.username(mgr.getId().toString())
			.password(mgr.getPassword())
			.roles("MANAGER")
			.build();
	}
}

