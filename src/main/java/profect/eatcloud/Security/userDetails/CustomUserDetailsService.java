package profect.eatcloud.Security.userDetails;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Admin.Entity.Admin;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Entity.Manager;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return customerRepository.findByEmail(email)
                .map(c -> new CustomUserDetails(c.getEmail(), c.getPassword(), "CUSTOMER"))
                .or(() -> managerRepository.findByEmail(email)
                        .map(m -> new CustomUserDetails(m.getEmail(), m.getPassword(), "MANAGER")))
                .or(() -> adminRepository.findByEmail(email)
                        .map(a -> new CustomUserDetails(a.getEmail(), a.getPassword(), "ADMIN")))
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
