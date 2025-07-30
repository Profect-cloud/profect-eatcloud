package profect.eatcloud.Domain.Customer.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Global.TimeData.BaseCodeRepository;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

public interface CustomerRepository extends BaseTimeRepository<Customer, UUID> {
	Optional<Customer> findByEmail(String email);
}
