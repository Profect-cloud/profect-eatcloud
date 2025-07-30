package profect.eatcloud.Login.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import profect.eatcloud.Domain.Admin.Entity.Admin;
import profect.eatcloud.Domain.Admin.Repository.AdminRepository;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Manager.Entity.Manager;
import profect.eatcloud.Domain.Manager.Repository.ManagerRepository;
import profect.eatcloud.Login.dto.LoginResponseDto;
import profect.eatcloud.Login.dto.SignupRequestDto;
import profect.eatcloud.Security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final CustomerRepository customerRepository;
	private final AdminRepository adminRepository;
	private final ManagerRepository managerRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	// 1) 로그인
	public LoginResponseDto login(String email, String password) {
		// 1) Admin 먼저 조회
		Admin admin = adminRepository.findByEmail(email)
			.orElse(null);
		if (admin != null) {
			if (!passwordEncoder.matches(password, admin.getPassword())) {
				throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
			}
			// Admin 전용 토큰(리프레시 토큰은 사용하지 않음)
			String accessToken = jwtTokenProvider.createToken(admin.getId(), "admin");
			return new LoginResponseDto(accessToken, null, "admin");
		}

		// 2) Manager 조회
		Manager manager = managerRepository.findByEmail(email)
			.orElse(null);
		if (manager != null) {
			if (!passwordEncoder.matches(password, manager.getPassword())) {
				throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
			}
			// 방문 로그 기록 등 필요시 추가
			// User/Manager용 Access + Refresh 토큰 생성
			String accessToken = jwtTokenProvider.createToken(manager.getId(), "manager");
			String refreshToken = jwtTokenProvider.createRefreshToken(manager.getId(), "manager");
			// 리프레시 토큰 DB에 저장 또는 갱신 (추후 활성화)
			// LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
			// refreshTokenService.saveOrUpdateToken(manager, refreshToken, expiryDate);
			return new LoginResponseDto(accessToken, refreshToken, "manager");
		}

		// 3) User 조회
		Customer customer = customerRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + email));

		if (!passwordEncoder.matches(password, customer.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		// 방문 로그 기록
		//visitLogService.logVisit(user.getId());

		// Access + Refresh 토큰 생성
		String accessToken = jwtTokenProvider.createToken(customer.getId(), "customer");
		String refreshToken = jwtTokenProvider.createRefreshToken(customer.getId(), "customer");

		// 리프레시 토큰 DB에 저장 또는 갱신
		//LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
		//refreshTokenService.saveOrUpdateToken(user, refreshToken, expiryDate);

		return new LoginResponseDto(accessToken, refreshToken, "customer");
	}

	// 2) 회원가입 (Customer 예시)
	public void signup(SignupRequestDto req) {
		if (customerRepository.findByEmail(req.getEmail()).isPresent()) {
			throw new RuntimeException("이미 존재하는 이메일입니다.");
		}
		Customer customer = Customer.builder()
			.email(req.getEmail())
			.password(passwordEncoder.encode(req.getPassword()))
			.name(req.getName())
			.nickname(req.getNickname())
			.build();

		customerRepository.save(customer);
	}

}
