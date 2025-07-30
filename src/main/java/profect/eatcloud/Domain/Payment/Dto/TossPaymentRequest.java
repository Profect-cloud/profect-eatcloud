package profect.eatcloud.Domain.Payment.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 토스페이먼츠 결제 승인 API 요청 DTO
 */
@Setter
@Getter
public class TossPaymentRequest {

    @JsonProperty("paymentKey")
    private String paymentKey;
    
    @JsonProperty("orderId") 
    private String orderId;
    
    @JsonProperty("amount")
    private Integer amount;

    public TossPaymentRequest() {}

    public TossPaymentRequest(String paymentKey, String orderId, Integer amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

}