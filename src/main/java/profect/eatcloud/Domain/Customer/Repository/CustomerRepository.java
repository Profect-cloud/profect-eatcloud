package profect.eatcloud.Domain.Customer.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import profect.eatcloud.Domain.Customer.Entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
	Optional<Customer> findByEmail(String email);
}
