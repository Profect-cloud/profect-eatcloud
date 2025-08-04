package profect.eatcloud.domain.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import profect.eatcloud.domain.payment.Exception.PaymentException;

import java.util.Base64;

@Service
public class TossPaymentService {

    @Autowired
    @Qualifier("tossWebClient")
    private WebClient tossWebClient;
    
    @Value("${toss.secret-key}")
    private String secretKey;

    public profect.eatcloud.domain.payment.dto.TossPaymentResponseDto confirmPayment(String paymentKey, String orderId, Integer amount) {
        validatePaymentRequest(paymentKey, orderId, amount);
        
        String encodedAuth = Base64.getEncoder()
            .encodeToString((secretKey + ":").getBytes());
        
        profect.eatcloud.domain.payment.dto.TossPaymentRequestDto request = new profect.eatcloud.domain.payment.dto.TossPaymentRequestDto(paymentKey, orderId, amount);
        
        try {
            profect.eatcloud.domain.payment.dto.TossPaymentResponseDto response = tossWebClient
                .post()
                .uri("/payments/confirm")
                .header("Authorization", "Basic " + encodedAuth)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(profect.eatcloud.domain.payment.dto.TossPaymentResponseDto.class)
                .block();
            return response;
            
        } catch (Exception e) {
            throw new PaymentException("결제 승인 중 오류가 발생했습니다: " + e.getMessage(), "PAYMENT_CONFIRM_ERROR", e);
        }
    }

    private void validatePaymentRequest(String paymentKey, String orderId, Integer amount) {
//        if (paymentKey == null || paymentKey.trim().isEmpty()) {
//        }
//
//        if (orderId == null || orderId.trim().isEmpty()) {
//        }
//
//        if (amount == null || amount <= 0) {
//        }
    }
}