package profect.eatcloud.Login.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;
import profect.eatcloud.Login.dto.LoginRequestDto;
import profect.eatcloud.Security.jwt.JwtUtil;
import profect.eatcloud.Security.userDetails.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class AuthService {

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
}
