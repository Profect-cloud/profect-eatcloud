package profect.eatcloud.Domain.Customer.Dto.response;

import profect.eatcloud.Domain.Customer.Entity.Customer;

import java.util.Objects;
import java.util.UUID;

public record CustomerProfileResponse(
	UUID id,
	String name,
	String nickname,
	String email,
	String phoneNumber,
	Integer points
) {
	public static CustomerProfileResponse from(Customer customer) {
		Objects.requireNonNull(customer, "Customer cannot be null");

		return new CustomerProfileResponse(
			customer.getId(),
			customer.getUsername(),
			customer.getNickname(),
			customer.getEmail(),
			customer.getPhoneNumber(),
			customer.getPoints()
		);
	}
}