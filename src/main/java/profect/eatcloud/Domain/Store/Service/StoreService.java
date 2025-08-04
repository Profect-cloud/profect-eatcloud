package profect.eatcloud.domain.Store.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.domain.Store.Dto.StoreResponseDto;
import profect.eatcloud.domain.Store.Dto.StoreSearchResponseDto;
import profect.eatcloud.domain.store.entity.Store;
import profect.eatcloud.domain.store.repository.MenuRepository_min;
import profect.eatcloud.domain.store.repository.StoreRepository_min;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository_min storeRepository;
    private final MenuRepository_min menuRepository;


    public List<StoreResponseDto> searchStoresByMenuCategory(String categoryCode) {
        List<Store> stores = menuRepository.findDistinctStoresByMenuCategoryCode(categoryCode);
        return stores.stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<StoreSearchResponseDto> searchStoresByCategoryAndDistance(UUID categoryId, double userLat, double userLon, double distanceKm) {
        return storeRepository.findStoresByCategoryWithinDistance(categoryId, userLat, userLon, distanceKm);
    }
}

