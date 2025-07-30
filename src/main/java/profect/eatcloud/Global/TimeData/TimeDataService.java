package profect.eatcloud.Global.TimeData;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeDataService {

	private final TimeDataRepository timeDataRepository;

	/** 새 이력 생성 */
	public TimeData createTimeData(String createdBy) {
		LocalDateTime now = LocalDateTime.now();
		TimeData td = TimeData.builder()
			.createdAt(now)
			.createdBy(createdBy)
			.updatedAt(now)
			.updatedBy(createdBy)
			.build();

		return timeDataRepository.save(td);
	}

	/** 수정 이력 업데이트 */
	public TimeData updateTimeData(UUID pTimeId, String updatedBy) {
		TimeData td = timeDataRepository.findById(pTimeId)
			.orElseThrow(() -> new IllegalArgumentException("TimeData not found: " + pTimeId));

		LocalDateTime now = LocalDateTime.now();
		td.setUpdatedAt(now);
		td.setUpdatedBy(updatedBy);

		return timeDataRepository.save(td);
	}

	/** soft delete 이력 설정 */
	public TimeData softDeleteTimeData(UUID pTimeId, String deletedBy) {
		TimeData td = timeDataRepository.findById(pTimeId)
			.orElseThrow(() -> new IllegalArgumentException("TimeData not found: " + pTimeId));

		LocalDateTime now = LocalDateTime.now();
		td.setDeletedAt(now);
		td.setDeletedBy(deletedBy);

		return timeDataRepository.save(td);
	}
}