package profect.eatcloud.Global.TimeData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import profect.eatcloud.Security.SecurityUtil;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseTimeRepository<T extends BaseTimeEntity, ID> extends JpaRepository<T, ID> {

	// 소프트 삭제
	default void softDelete(ID id) {
		findByIdIncludingDeleted(id).ifPresent(entity -> {
			String user = SecurityUtil.getCurrentUsername();
			TimeDataService timeDataService = BeanUtil.getBean(TimeDataService.class);
			timeDataService.softDeleteTimeData(entity.getTimeData().getPTimeId(), user);
		});
	}

	default void softDelete(T entity) {
		String user = SecurityUtil.getCurrentUsername();
		TimeDataService timeDataService = BeanUtil.getBean(TimeDataService.class);
		timeDataService.softDeleteTimeData(entity.getTimeData().getPTimeId(), user);
	}

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
		softDelete(entity);
	}

	@Override
	default void deleteById(ID id) {
		softDelete(id);
	}

	@Override
	default void deleteAll(Iterable<? extends T> entities) {
		entities.forEach(this::softDelete);
	}

	@Override
	default void deleteAll() {
		findAllIncludingDeleted().forEach(this::softDelete);
	}
}