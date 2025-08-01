package profect.eatcloud.Domain.Customer.Dto.response;

import java.util.UUID;

public record AddressResponseDto(
	UUID id,
	String zipcode,
	String roadAddr,
	String detailAddr,
	Boolean isSelected
) {}