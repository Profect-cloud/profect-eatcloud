package profect.eatcloud.Domain.User.Dto.response;

import lombok.Data;

@Data
public class AddressResponseDto {
    private String id;
    private String zipcode;
    private String roadAddr;
    private String detailAddr;
    private Boolean isSelected;
} 