package profect.eatcloud.Domain.Store.Dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
public class StoreResponseDto {
    private UUID storeId;
    private String storeName;
    private String storeAddress;
    private String phoneNumber;
    private UUID categoryId;
    private int minCost;
    private String description;
    private Double storeLat;
    private Double storeLon;
    private Boolean openStatus;
    private LocalTime openTime;
    private LocalTime closeTime;
    private UUID pTimeId;
}
