package profect.eatcloud.domain.payment.service;
import org.springframework.beans.factory.annotation.Value;



public class PaymentCheckoutService {
    @Value("${toss.client-key}")
    private String clientKey;

    public String getClientKey() {
        return clientKey;
    }


}
