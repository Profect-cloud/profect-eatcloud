package profect.eatcloud.Domain.Manager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.Manager.Entity.Manager;

import java.util.Optional;
import java.util.UUID;

public interface ManagerRepository extends JpaRepository<Manager, UUID> {
    Optional<Manager> findByUsername(String username);
}
