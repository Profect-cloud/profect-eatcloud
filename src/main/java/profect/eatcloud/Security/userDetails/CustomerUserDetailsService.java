package profect.eatcloud.Security.userDetails;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;

@RequiredArgsConstructor
@Service
public class CustomerUserDetailsService implements UserDetailsService {

	private final CustomerRepository customerRepository;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		UUID userId;
		try {
			userId = UUID.fromString(id);
		} catch (IllegalArgumentException ex) {
			throw new UsernameNotFoundException("유효하지 않은 사용자 ID 형식입니다: " + id);
		}

		Customer user = customerRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + id));

		return org.springframework.security.core.userdetails.User.builder()
			.username(String.valueOf(user.getId()))
			.password(user.getPassword())
			.roles("USER")  // 권한 부여
			.build();
	}
}
