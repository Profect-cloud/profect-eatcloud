package profect.eatcloud.domain.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import profect.eatcloud.domain.store.dto.StoreSearchByMenuCategoryRequestDto;
import profect.eatcloud.domain.store.dto.StoreSearchResponseDto;
import profect.eatcloud.domain.store.repository.StoreRepository_min;

import java.util.List;
import java.util.UUID;

@Service
public class StoreService {

    private final StoreRepository_min storeRepository;

    @Autowired
    public StoreService(StoreRepository_min storeRepository) {
        this.storeRepository = storeRepository;
    }

    public List<StoreSearchResponseDto> searchStoresByCategoryAndDistance(UUID categoryId, double userLat, double userLon, double distanceKm) {
        return storeRepository.findStoresByCategoryWithinDistance(categoryId, userLat, userLon, distanceKm);
    }

    public List<StoreSearchResponseDto> searchStoresByMenuCategory(StoreSearchByMenuCategoryRequestDto request) {
        return storeRepository.findStoresByMenuCategoryWithinDistance(
                request.getCategoryCode(),
                request.getLat(),
                request.getLon(),
                request.getRadius()
        );
    }
}
