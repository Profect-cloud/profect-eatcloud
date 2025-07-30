package profect.eatcloud.Domain.Payment.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "p_payment_requests")
public class PaymentRequest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_request_id")
    private java.util.UUID paymentRequestId;

    @Column(name = "order_id", nullable = false)
    private java.util.UUID orderId;

    @Column(name = "pg_provider", nullable = false, length = 100)
    private String pgProvider;

    @Column(name = "request_payload", nullable = false, columnDefinition = "text")
    private String requestPayload;  // JSON 문자열로 저장

    @Column(name = "redirect_url", columnDefinition = "text")
    private String redirectUrl;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "failure_reason", columnDefinition = "text")
    private String failureReason;

    // 기본 생성자
    public PaymentRequest() {}

    // 생성자
    public PaymentRequest(java.util.UUID orderId, String pgProvider, String requestPayload) {
        this.orderId = orderId;
        this.pgProvider = pgProvider;
        this.requestPayload = requestPayload;
        this.status = "PENDING";
        this.requestedAt = LocalDateTime.now();
    }

}