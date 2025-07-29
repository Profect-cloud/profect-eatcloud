package profect.eatcloud.Login.service;

<<<<<<< HEAD
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;
import profect.eatcloud.Login.dto.LoginRequestDto;
import profect.eatcloud.Security.jwt.JwtUtil;
import profect.eatcloud.Security.userDetails.CustomUserDetails;
=======
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
>>>>>>> d974436be3d6aa74dec64244dac7a084c0036739

@Service
@RequiredArgsConstructor
public class AuthService {

<<<<<<< HEAD
	private final CustomerRepository customerRepository;
	private final ManagerRepository managerRepository;
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public String login(LoginRequestDto request) {
		String username = request.getUsername();
		String password = request.getPassword();
		String role = request.getRole();

		CustomUserDetails account = switch (role.toUpperCase()) {
			case "CUSTOMER" -> customerRepository.findByUsername(username).orElseThrow();
			case "MANAGER" -> managerRepository.findByUsername(username).orElseThrow();
			case "ADMIN" -> adminRepository.findByUsername(username).orElseThrow();
			default -> throw new IllegalArgumentException("Invalid role: " + role);
		};

		if (!passwordEncoder.matches(password, account.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}

		return jwtUtil.generateToken(account.getUsername(), role);
	}
=======
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
>>>>>>> d974436be3d6aa74dec64244dac7a084c0036739
}
