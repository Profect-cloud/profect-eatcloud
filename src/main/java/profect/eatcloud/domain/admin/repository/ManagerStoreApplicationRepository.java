package profect.eatcloud.domain.admin.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import profect.eatcloud.Global.TimeData.BaseTimeRepository;
import profect.eatcloud.domain.admin.entity.ManagerStoreApplication;

@Repository
public interface ManagerStoreApplicationRepository extends BaseTimeRepository<ManagerStoreApplication, UUID> {

}