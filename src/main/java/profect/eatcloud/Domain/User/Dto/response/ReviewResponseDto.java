package profect.eatcloud.Domain.User.Dto.response;

import lombok.Data;

@Data
public class ReviewResponseDto {
    private String reviewId;
    private String orderId;
    private Double rating;
    private String content;
} 