package profect.eatcloud.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;
import profect.eatcloud.Global.TimeData.TimeData;

@Getter
@Builder
public class CategoryDto {
	private final Object id;
	private final String name;
	private final Integer sortOrder;
	private final Boolean isActive;
	private final TimeData timeData;
}