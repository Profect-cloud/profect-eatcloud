package profect.eatcloud.Domain.Admin.Repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import profect.eatcloud.Domain.Admin.Entity.ManagerStoreApplication;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

@Repository
public interface ManagerStoreApplicationRepository extends BaseTimeRepository<ManagerStoreApplication, UUID> {

}
