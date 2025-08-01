package profect.eatcloud.Domain.Store.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Store.Dto.StoreResponseDto;
import profect.eatcloud.Domain.Store.Dto.StoreSearchResponseDto;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Domain.Store.Repository.MenuRepository_min;
import profect.eatcloud.Domain.Store.Repository.StoreRepository_min;

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

