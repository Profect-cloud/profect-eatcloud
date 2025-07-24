package profect.eatcloud.Domain.Global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.Global.entity.Time;
import java.util.UUID;

public interface TimeRepository extends JpaRepository<Time, UUID> {
} 