package profect.eatcloud.Domain.Store.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import profect.eatcloud.Domain.Store.Dto.StoreSearchResponseDto;

import java.util.List;
import java.util.UUID;

@Repository
public class StoreCustomRepositoryImpl implements StoreCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<StoreSearchResponseDto> findStoresByCategoryWithinDistance(
            UUID categoryId, double userLat, double userLon, double distanceKm) {

        String sql = """
            SELECT 
                s.store_id,
                s.store_name,
                s.store_address,
                s.store_lat,
                s.store_lon,
                s.min_cost,
                s.open_status
            FROM p_stores s
            WHERE ST_DistanceSphere(
                ST_MakePoint(s.store_lon, s.store_lat),
                ST_MakePoint(:userLon, :userLat)
            ) <= (:distanceKm * 1000)
            AND s.category_id = :categoryId
        """;

        return em.createNativeQuery(sql, "StoreSearchResponseMapping")
                .setParameter("userLat", userLat)
                .setParameter("userLon", userLon)
                .setParameter("distanceKm", distanceKm)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }
}
