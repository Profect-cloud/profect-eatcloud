package profect.eatcloud.Domain.Admin.Dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
	private UUID categoryId;
	private String categoryName;
	private Integer sortOrder;
	private Boolean isActive;
}