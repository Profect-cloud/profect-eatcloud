package profect.eatcloud.Domain.Admin.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import profect.eatcloud.Domain.Admin.entity.ManagerStoreApplication;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

@Repository
public interface ManagerStoreApplicationRepository extends BaseTimeRepository<ManagerStoreApplication, UUID> {

}