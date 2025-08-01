package profect.eatcloud.Domain.Order.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import profect.eatcloud.Domain.Order.Entity.Review;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends BaseTimeRepository<Review, UUID> {

	boolean existsByOrderOrderIdAndTimeData_DeletedAtIsNull(UUID orderId);

	@Query("SELECT r FROM Review r JOIN r.order o WHERE o.customerId = :customerId AND r.timeData.deletedAt IS NULL ORDER BY r.timeData.createdAt DESC")
	List<Review> findByOrderCustomerIdAndTimeData_DeletedAtIsNullOrderByTimeData_CreatedAtDesc(@Param("customerId") UUID customerId);

	@Query("SELECT r FROM Review r JOIN r.order o WHERE o.storeId = :storeId AND r.timeData.deletedAt IS NULL ORDER BY r.timeData.createdAt DESC")
	List<Review> findByOrderStoreIdAndTimeData_DeletedAtIsNullOrderByTimeData_CreatedAtDesc(@Param("storeId") UUID storeId);

	@Query("SELECT r FROM Review r JOIN r.order o WHERE r.reviewId = :reviewId AND o.customerId = :customerId AND r.timeData.deletedAt IS NULL")
	Optional<Review> findByReviewIdAndOrderCustomerIdAndTimeData_DeletedAtIsNull(
		@Param("reviewId") UUID reviewId,
		@Param("customerId") UUID customerId
	);
}