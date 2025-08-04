package profect.eatcloud.domain.payment.service;

import profect.eatcloud.domain.payment.dto.CheckoutRequestDto;
import profect.eatcloud.domain.payment.dto.CheckoutResponseDto;

public class PaymentOrderService {
    public CheckoutResponseDto processCheckout(CheckoutRequestDto request);
    public String convertOrderTypeToCode(String displayName);
}
