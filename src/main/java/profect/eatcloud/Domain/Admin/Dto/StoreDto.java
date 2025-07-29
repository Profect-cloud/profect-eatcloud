package profect.eatcloud.Domain.Admin.Dto;

import java.time.LocalTime;
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
public class StoreDto {
	private UUID storeId;
	private String storeName;
	private UUID categoryId;
	private Integer minCost;
	private String description;
	private Double storeLat;
	private Double storeLon;
	private Boolean openStatus;
	private LocalTime openTime;
	private LocalTime closeTime;
}
