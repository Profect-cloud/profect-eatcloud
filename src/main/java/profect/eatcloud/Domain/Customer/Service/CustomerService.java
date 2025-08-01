package profect.eatcloud.Domain.Customer.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import profect.eatcloud.Domain.Customer.Dto.request.CustomerProfileUpdateRequestDto;
import profect.eatcloud.Domain.Customer.Dto.request.CustomerWithdrawRequestDto;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerProfileResponseDto;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerWithdrawResponseDto;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CustomerService {

	@Autowired
	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = Objects.requireNonNull(customerRepository, "CustomerRepository cannot be null");
	}

	public Customer getCustomer(UUID customerId) {
		Objects.requireNonNull(customerId, "Customer ID cannot be null");
		return customerRepository.findById(customerId)
			.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));
	}

	@Transactional
	public CustomerProfileResponseDto updateCustomer(UUID customerId, CustomerProfileUpdateRequestDto request) {
		Objects.requireNonNull(customerId, "Customer ID cannot be null");
		Objects.requireNonNull(request, "Update request cannot be null");

		Customer customer = customerRepository.findById(customerId)
			.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));

		applyProfileUpdates(customer, request);
		Customer savedCustomer = customerRepository.save(customer);
		return CustomerProfileResponseDto.from(savedCustomer);
	}

	@Transactional
	public CustomerWithdrawResponseDto withdrawCustomer(UUID customerId, CustomerWithdrawRequestDto request) {
		Objects.requireNonNull(customerId, "Customer ID cannot be null");
		Objects.requireNonNull(request, "Withdraw request cannot be null");

		Customer customer = customerRepository.findById(customerId)
			.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));

		customerRepository.deleteById(customerId);
		return CustomerWithdrawResponseDto.of(LocalDateTime.now());
	}

	private void applyProfileUpdates(Customer customer, CustomerProfileUpdateRequestDto request) {
		Optional.ofNullable(request.getNickname())
			.ifPresent(customer::setNickname);
		Optional.ofNullable(request.getEmail())
			.ifPresent(customer::setEmail);
		Optional.ofNullable(request.getPhoneNumber())
			.ifPresent(customer::setPhoneNumber);
	}
}