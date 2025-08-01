package profect.eatcloud.Domain.Admin.Repository;

import java.util.Optional;
import java.util.UUID;

import profect.eatcloud.Domain.Admin.Entity.Admin;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

public interface AdminRepository extends BaseTimeRepository<Admin, UUID> {
	Optional<Admin> findByEmail(String email);
}
