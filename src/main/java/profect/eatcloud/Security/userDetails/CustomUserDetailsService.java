package profect.eatcloud.Security.userDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 순서대로 탐색 (우선순위 설정 가능)
        return customerRepository.findByUsername(username).map(customer -> (UserDetails) customer)
                .or(() -> managerRepository.findByUsername(username).map(manager -> (UserDetails) manager))
                .or(() -> adminRepository.findByUsername(username).map(admin -> (UserDetails) admin))
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
