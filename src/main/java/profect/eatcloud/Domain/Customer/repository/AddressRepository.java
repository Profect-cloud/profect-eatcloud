package profect.eatcloud.Domain.Customer.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import profect.eatcloud.Domain.Customer.Entity.Address;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

public interface AddressRepository extends BaseTimeRepository<Address, UUID> {
	List<Address> findByCustomerIdAndTimeData_DeletedAtIsNull(UUID customerId);

	Optional<Address> findByCustomerIdAndIsSelectedTrueAndTimeData_DeletedAtIsNull(UUID customerId);

	@Query("SELECT a FROM Address a WHERE a.id = :id AND a.customer.id = :customerId AND a.timeData.deletedAt IS NULL")
	Optional<Address> findByIdAndCustomerId(@Param("id") UUID id, @Param("customerId") UUID customerId);
}
