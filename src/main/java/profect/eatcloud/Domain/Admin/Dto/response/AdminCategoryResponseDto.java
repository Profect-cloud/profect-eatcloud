package profect.eatcloud.Domain.Admin.Dto.response;

import lombok.Data;

@Data
public class AdminCategoryResponseDto {
    private String categoryId;
    private String categoryName;
    private Integer sortOrder;
    // Add more fields as needed
} 