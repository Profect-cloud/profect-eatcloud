package profect.eatcloud.Domain.Store.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import profect.eatcloud.Domain.Store.Entity.Store;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository_min extends BaseTimeRepository<Store, UUID>, StoreCustomRepository{

    // 메뉴 등록 시 storeId로 가게 정보 조회
    Optional<Store> findById(UUID storeId);


    // (선택) 특정 사장님이 소유한 가게인지 확인할 때
    //Optional<Store> findByIdAndOwnerId(UUID storeId, UUID ownerId);
    //Optional<Store> findByStoreIdAndOwnerId(UUID storeId, UUID ownerId);

    // (선택) 단순히 존재만 확인할 때
    //boolean existsById(UUID storeId);
}