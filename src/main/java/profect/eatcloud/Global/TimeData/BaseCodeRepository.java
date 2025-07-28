package profect.eatcloud.Global.TimeData;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseCodeRepository<T extends BaseTimeEntity, ID> extends BaseTimeRepository<T, ID> {

	// is_active 필드를 가진 코드성 테이블용 (assumming isActive field exists)
	@Query("SELECT e FROM #{#entityName} e WHERE e.timeData.deletedAt IS NULL AND e.isActive = true")
	@Override
	List<T> findAll();

	@Query("SELECT e FROM #{#entityName} e WHERE e.timeData.deletedAt IS NULL AND e.isActive = true AND e.id = ?1")
	@Override
	Optional<T> findById(ID id);

	// 활성 상태 코드만 조회 (정렬 포함)
	@Query("SELECT e FROM #{#entityName} e WHERE e.timeData.deletedAt IS NULL AND e.isActive = true ORDER BY e.sortOrder")
	List<T> findAllActiveSorted();
}