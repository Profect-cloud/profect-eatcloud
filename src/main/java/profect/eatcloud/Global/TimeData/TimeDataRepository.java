package profect.eatcloud.Global.TimeData;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TimeDataRepository extends JpaRepository<TimeData, UUID> {
}
