package profect.eatcloud.domain.Store.Repository;

import profect.eatcloud.domain.Store.Dto.StoreSearchResponseDto;

import java.util.List;
import java.util.UUID;

public interface StoreCustomRepository {
    List<StoreSearchResponseDto> findStoresByCategoryWithinDistance(
            UUID categoryId, double userLat, double userLon, double distanceKm
    );
}

