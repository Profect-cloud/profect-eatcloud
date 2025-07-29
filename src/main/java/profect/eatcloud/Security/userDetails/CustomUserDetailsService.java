package profect.eatcloud.Security.userDetails;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private CustomerRepository customerRepo;
    @Autowired private ManagerRepository managerRepo;
    @Autowired private AdminRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Customer> c = customerRepo.findByEmail(email);
        if (c.isPresent()) {
            return new CustomerPrincipal(c.get());
        }
        Optional<Manager> m = managerRepo.findByEmail(email);
        if (m.isPresent()) {
            return new ManagerPrincipal(m.get());
        }
        Optional<Admin> a = adminRepo.findByEmail(email);
        if (a.isPresent()) {
            return new AdminPrincipal(a.get());
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}
