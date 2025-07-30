package profect.eatcloud.Domain.Customer.Repository;

import java.util.Optional;
import java.util.UUID;

import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Global.TimeData.BaseCodeRepository;


public interface CustomerRepository extends BaseTimeRepository<Customer, UUID> {
	Optional<Customer> findByEmail(String email);
}
