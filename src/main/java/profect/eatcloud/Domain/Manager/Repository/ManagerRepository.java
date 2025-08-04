package profect.eatcloud.domain.manager.repository;

import java.util.Optional;
import java.util.UUID;

import profect.eatcloud.domain.manager.entity.Manager;
import profect.eatcloud.global.timedata.BaseTimeRepository;

public interface ManagerRepository extends BaseTimeRepository<Manager, UUID> {
	Optional<Manager> findByEmail(String email);
}
