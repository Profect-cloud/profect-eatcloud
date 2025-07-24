package profect.eatcloud.Domain.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.User.Entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
} 