package profect.eatcloud.Domain.Admin.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.User.Entity.User;
import java.util.UUID;

public interface AdminUserRepository extends JpaRepository<User, UUID> {
} 