package profect.eatcloud.Domain.Customer.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmail(String email);
    Boolean existsByEmail(String email);
	Optional<Customer> findByUsername(String username);
}
