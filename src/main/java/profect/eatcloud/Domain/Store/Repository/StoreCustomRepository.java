package profect.eatcloud.Domain.Store.Repository;

import profect.eatcloud.Domain.Store.Dto.StoreSearchResponseDto;

import java.util.List;
import java.util.UUID;

public interface StoreCustomRepository {
    List<StoreSearchResponseDto> findStoresByCategoryWithinDistance(
            UUID categoryId, double userLat, double userLon, double distanceKm
    );
}

