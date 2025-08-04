package profect.eatcloud.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import profect.eatcloud.domain.customer.entity.Customer;
import profect.eatcloud.domain.customer.repository.CustomerRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {
    
    private final CustomerRepository customerRepository;
    
    /**
     * 현재 인증된 고객 정보 조회
     */
    public Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("인증되지 않은 사용자입니다.");
        }

        String customerIdStr = authentication.getName();
        try {
            UUID customerId = UUID.fromString(customerIdStr);
            return customerRepository.findById(customerId)
                    .orElseThrow(() -> new AuthenticationException("고객 정보를 찾을 수 없습니다."));
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("유효하지 않은 고객 ID 형식입니다.");
        }
    }
    
    /**
     * 현재 인증된 고객 ID 조회
     */
    public UUID getCurrentCustomerId() {
        return getCurrentCustomer().getId();
    }
    
    /**
     * 현재 인증된 고객 ID를 문자열로 조회
     */
    public String getCurrentCustomerIdAsString() {
        return getCurrentCustomerId().toString();
    }
    
    /**
     * 고객 ID 검증 (현재 인증된 고객과 비교)
     */
    public boolean validateCustomerId(String customerIdStr) {
        try {
            Customer currentCustomer = getCurrentCustomer();
            return currentCustomer.getId().toString().equals(customerIdStr);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 고객 ID 검증 (현재 인증된 고객과 비교) - UUID 버전
     */
    public boolean validateCustomerId(UUID customerId) {
        try {
            Customer currentCustomer = getCurrentCustomer();
            return currentCustomer.getId().equals(customerId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 테스트 고객 여부 확인
     */
    public boolean isTestCustomer(String customerIdStr) {
        return "test-customer".equals(customerIdStr) || 
               "11111111-1111-1111-1111-111111111111".equals(customerIdStr);
    }
    
    /**
     * 인증 상태 확인
     */
    public boolean isAuthenticated() {
        try {
            getCurrentCustomer();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 커스텀 인증 예외
     */
    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
        
        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
