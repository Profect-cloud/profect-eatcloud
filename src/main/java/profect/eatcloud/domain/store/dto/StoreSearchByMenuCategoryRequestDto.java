package profect.eatcloud.domain.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class StoreSearchByMenuCategoryRequestDto {
    private String categoryCode;  // 예: "김밥"
    private double lat;
    private double lon;
    private double radius; // km 단위

    public StoreSearchByMenuCategoryRequestDto(String categoryCode, double userLat, double userLon, double distanceKm) {
    }
}