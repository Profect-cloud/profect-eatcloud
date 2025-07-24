package profect.eatcloud.Domain.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profect.eatcloud.Domain.User.Entity.Address;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
} 