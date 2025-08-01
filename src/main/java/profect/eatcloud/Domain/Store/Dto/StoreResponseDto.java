package profect.eatcloud.Domain.Store.Dto;

import lombok.Builder;
import lombok.Getter;
import profect.eatcloud.Domain.Store.Entity.Store;

import java.util.UUID;

@Getter
@Builder
public class StoreResponseDto {
    private UUID storeId;
    private String storeName;
    private String storeAddress;
    private String phoneNumber;
    private Integer minCost;
    private String description;
    private Double storeLat;
    private Double storeLon;
    private Boolean openStatus;

    public static StoreResponseDto from(Store store) {
        return StoreResponseDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .storeAddress(store.getStoreAddress())
                .phoneNumber(store.getPhoneNumber())
                .minCost(store.getMinCost())
                .description(store.getDescription())
                .storeLat(store.getStoreLat())
                .storeLon(store.getStoreLon())
                .openStatus(store.getOpenStatus())
                .build();
    }
}

