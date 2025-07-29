package profect.eatcloud.Domain.Customer.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Customer.Dto.request.CustomerProfileUpdateRequest;
import profect.eatcloud.Domain.Customer.Dto.request.CustomerWithdrawRequest;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerProfileResponse;
import profect.eatcloud.Domain.Customer.Dto.response.CustomerWithdrawResponse;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Service.CustomerService;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = Objects.requireNonNull(customerService, "CustomerService cannot be null");
	}

	@GetMapping("/{id}")
	public ResponseEntity<CustomerProfileResponse> getCustomer(@PathVariable UUID id) {
		Customer customer = customerService.getCustomer(id);
		return ResponseEntity.ok(CustomerProfileResponse.from(customer));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<CustomerProfileResponse> updateCustomer(
		@PathVariable UUID id,
		@Valid @RequestBody CustomerProfileUpdateRequest request) {

		CustomerProfileResponse response = customerService.updateCustomer(id, request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<CustomerWithdrawResponse> withdrawCustomer(
		@PathVariable UUID id,
		@Valid @RequestBody CustomerWithdrawRequest request) {

		CustomerWithdrawResponse response = customerService.withdrawCustomer(id, request);
		return ResponseEntity.ok(response);
	}
}