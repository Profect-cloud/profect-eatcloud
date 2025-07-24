package profect.eatcloud.Domain.User.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage/reviews")
public class ReviewController {
    @GetMapping
    public void getReviews(org.springframework.security.core.Authentication authentication) {}

    @DeleteMapping("/{review_id}")
    public void deleteReview(org.springframework.security.core.Authentication authentication, @PathVariable String review_id) {}
} 