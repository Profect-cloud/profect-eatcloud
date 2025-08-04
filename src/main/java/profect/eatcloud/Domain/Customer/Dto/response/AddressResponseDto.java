package profect.eatcloud.domain.customer.Dto.response;

import java.util.UUID;

public record AddressResponseDto(
	UUID id,
	String zipcode,
	String roadAddr,
	String detailAddr,
	Boolean isSelected
) {}