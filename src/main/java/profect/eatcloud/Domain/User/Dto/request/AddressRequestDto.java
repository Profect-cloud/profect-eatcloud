package profect.eatcloud.Domain.User.Dto.request;

import lombok.Data;

@Data
public class AddressRequestDto {
    private String zipcode;
    private String roadAddr;
    private String detailAddr;
    private Boolean isSelected;
} 