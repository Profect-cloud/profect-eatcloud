package profect.eatcloud.Domain.User.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @Column(name = "review_id")
    private UUID reviewId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;
} 