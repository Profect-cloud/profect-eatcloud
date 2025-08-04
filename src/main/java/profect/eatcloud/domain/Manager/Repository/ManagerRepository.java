package profect.eatcloud.domain.Manager.Repository;

import java.util.Optional;
import java.util.UUID;

import profect.eatcloud.domain.Manager.Entity.Manager;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

public interface ManagerRepository extends BaseTimeRepository<Manager, UUID> {
	Optional<Manager> findByEmail(String email);
}
