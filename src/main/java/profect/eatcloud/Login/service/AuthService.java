package profect.eatcloud.Login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Login.dto.SignupDto;
import profect.eatcloud.Security.jwt.JwtUtil;
import profect.eatcloud.Security.userDetails.CustomUserDetailsService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final CustomUserDetailsService customUserDetailsService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public void signup(SignupDto signupDto){
        if (customerRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        Customer customer = new Customer();
        customer.setEmail(signupDto.getEmail());
        customer.setPassword(bCryptPasswordEncoder.encode(signupDto.getPassword()));
        customer.setName(signupDto.getName());
        customer.setNickname(signupDto.getNickname());
        customer.setPhoneNumber(signupDto.getPhone());
        customerRepository.save(customer);
    }

    public String login(String role, String email, String rawPassword) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (!bCryptPasswordEncoder.matches(rawPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.generateToken(userDetails.getUsername(), role);
    }
}
