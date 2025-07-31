package profect.eatcloud.Domain.Customer.Repository;

import java.util.Optional;
import java.util.UUID;

import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;


public interface CustomerRepository extends BaseTimeRepository<Customer, UUID> {
	Optional<Customer> findByEmail(String email);

	Optional<Customer> findByNameAndTimeData_DeletedAtIsNull(String name);
	Optional<Customer> findByEmailAndTimeData_DeletedAtIsNull(String email);
	boolean existsByNameAndTimeData_DeletedAtIsNull(String name);
}
