package profect.eatcloud.Domain.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.User.Entity.Review;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
} 