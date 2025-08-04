package profect.eatcloud.domain.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import profect.eatcloud.domain.customer.dto.request.AddressRequestDto;
import profect.eatcloud.domain.customer.dto.response.AddressResponseDto;
import profect.eatcloud.domain.customer.entity.Address;
import profect.eatcloud.domain.customer.entity.Customer;
import profect.eatcloud.domain.customer.repository.AddressRepository;
import profect.eatcloud.domain.customer.repository.CustomerRepository;
import profect.eatcloud.security.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

	private final AddressRepository addressRepository;
	private final CustomerRepository customerRepository;

	public List<AddressResponseDto> getAddressList(UUID customerId) {
		List<Address> addresses = addressRepository.findByCustomerIdAndTimeData_DeletedAtIsNull(customerId);
		return addresses.stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}

	@Transactional
	public AddressResponseDto createAddress(UUID customerId, AddressRequestDto request) {
		Customer customer = customerRepository.findById(customerId)
			.orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

		// 첫 번째 주소인지 확인
		List<Address> existingAddresses = addressRepository.findByCustomerIdAndTimeData_DeletedAtIsNull(customerId);
		boolean isFirstAddress = existingAddresses.isEmpty();

		Address address = Address.builder()
			.zipcode(request.zipcode())
			.roadAddr(request.roadAddr())
			.detailAddr(request.detailAddr())
			.isSelected(isFirstAddress)
			.customer(customer)
			.build();

		Address savedAddress = addressRepository.save(address);
		return toResponse(savedAddress);
	}

	@Transactional
	public AddressResponseDto updateAddress(UUID customerId, UUID addressId, AddressRequestDto request) {
		Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
			.orElseThrow(() -> new IllegalArgumentException("Address not found or not authorized"));

		address.updateAddress(request.zipcode(), request.roadAddr(), request.detailAddr());

		Address updatedAddress = addressRepository.save(address);
		return toResponse(updatedAddress);
	}

	@Transactional
	public void deleteAddress(UUID customerId, UUID addressId) {
		Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
			.orElseThrow(() -> new IllegalArgumentException("Address not found or not authorized"));

		addressRepository.softDeleteByTimeId(
			address.getTimeData().getPTimeId(),
			LocalDateTime.now(),
			SecurityUtil.getCurrentUsername()
		);
	}

	@Transactional
	public void setDefaultAddress(UUID customerId, UUID addressId) {
		Address targetAddress = addressRepository.findByIdAndCustomerId(addressId, customerId)
			.orElseThrow(() -> new IllegalArgumentException("Address not found or not authorized"));

		// 이미 기본 주소인 경우 처리하지 않음
		if (targetAddress.getIsSelected()) {
			return;
		}

		// 현재 기본 주소 해제
		addressRepository.findByCustomerIdAndIsSelectedTrueAndTimeData_DeletedAtIsNull(customerId)
			.ifPresent(currentDefault -> {
				currentDefault.changeSelected(false);
				addressRepository.save(currentDefault);
			});

		// 새로운 기본 주소 설정
		targetAddress.changeSelected(true);
		addressRepository.save(targetAddress);
	}

	private AddressResponseDto toResponse(Address address) {
		return new AddressResponseDto(
			address.getId(),
			address.getZipcode(),
			address.getRoadAddr(),
			address.getDetailAddr(),
			address.getIsSelected()
		);
	}
}