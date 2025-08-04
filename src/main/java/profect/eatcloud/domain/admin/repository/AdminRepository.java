package profect.eatcloud.domain.admin.repository;

import java.util.Optional;
import java.util.UUID;

import profect.eatcloud.Global.TimeData.BaseTimeRepository;
import profect.eatcloud.domain.admin.entity.Admin;

public interface AdminRepository extends BaseTimeRepository<Admin, UUID> {
	Optional<Admin> findByEmail(String email);
}