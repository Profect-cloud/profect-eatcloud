package profect.eatcloud.Domain.Payment.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TossPaymentResponse {
    
    @JsonProperty("paymentKey")
    private String paymentKey;
    
    @JsonProperty("orderId")
    private String orderId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private Integer totalAmount;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("requestedAt")
    private String requestedAt;
    
    @JsonProperty("approvedAt")
    private String approvedAt;

    public TossPaymentResponse() {}

}