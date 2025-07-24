package profect.eatcloud.Domain.Admin.Dto.request;

import lombok.Data;

@Data
public class AdminCategoryRequestDto {
    private String categoryName;
    private Integer sortOrder;
    // Add more fields as needed
} 