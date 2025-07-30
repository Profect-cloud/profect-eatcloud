package profect.eatcloud.Domain.Manager.Repository;

import java.util.Optional;
import java.util.UUID;

import profect.eatcloud.Domain.Manager.Entity.Manager;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

public interface ManagerRepository extends BaseTimeRepository<Manager, UUID> {
	Optional<Manager> findByEmail(String email);
}
