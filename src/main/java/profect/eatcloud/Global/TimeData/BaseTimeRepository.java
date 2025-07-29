package profect.eatcloud.Global.TimeData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import profect.eatcloud.Security.SecurityUtil;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseTimeRepository<T extends BaseTimeEntity, ID> extends JpaRepository<T, ID> {

	// 소프트 삭제를 위한 업데이트 쿼리
	@Modifying
	@Query("UPDATE TimeData td SET td.deletedAt = :deletedAt, td.deletedBy = :deletedBy " +
		"WHERE td.pTimeId IN (SELECT e.timeData.pTimeId FROM #{#entityName} e WHERE e.id = :id)")
	void softDeleteById(@Param("id") ID id, @Param("deletedAt") Instant deletedAt, @Param("deletedBy") String deletedBy);

	// TimeData의 UUID를 통한 소프트 삭제
	@Modifying
	@Query("UPDATE #{#entityName} e SET e.timeData.deletedAt = :deletedAt, e.timeData.deletedBy = :deletedBy WHERE e.timeData.pTimeId = :timeId")
	void softDeleteByTimeId(@Param("timeId") java.util.UUID timeId, @Param("deletedAt") Instant deletedAt, @Param("deletedBy") String deletedBy);

	// 기본 조회: 삭제되지 않은 데이터만
	@Query("SELECT e FROM #{#entityName} e WHERE e.timeData.deletedAt IS NULL")
	@Override
	List<T> findAll();

	@Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1 AND e.timeData.deletedAt IS NULL")
	@Override
	Optional<T> findById(ID id);

	// 삭제된 데이터 포함 조회
	@Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1")
	Optional<T> findByIdIncludingDeleted(ID id);

	@Query("SELECT e FROM #{#entityName} e")
	List<T> findAllIncludingDeleted();

	// 삭제된 데이터만 조회
	@Query("SELECT e FROM #{#entityName} e WHERE e.timeData.deletedAt IS NOT NULL")
	List<T> findDeleted();

	// 기본 delete를 소프트 삭제로 오버라이드
	@Override
	default void delete(T entity) {
		if (entity != null && entity.getTimeData() != null) {
			String user = SecurityUtil.getCurrentUsername();
			softDeleteByTimeId(entity.getTimeData().getPTimeId(), Instant.now(), user);
		}
	}

	@Override
	default void deleteById(ID id) {
		String user = SecurityUtil.getCurrentUsername();
		softDeleteById(id, Instant.now(), user);
	}

	@Override
	default void deleteAll(Iterable<? extends T> entities) {
		String user = SecurityUtil.getCurrentUsername();
		Instant now = Instant.now();
		entities.forEach(entity -> {
			if (entity != null && entity.getTimeData() != null) {
				softDeleteByTimeId(entity.getTimeData().getPTimeId(), now, user);
			}
		});
	}

	@Override
	default void deleteAll() {
		String user = SecurityUtil.getCurrentUsername();
		Instant now = Instant.now();
		findAllIncludingDeleted().forEach(entity -> {
			if (entity != null && entity.getTimeData() != null) {
				softDeleteByTimeId(entity.getTimeData().getPTimeId(), now, user);
			}
		});
	}
}