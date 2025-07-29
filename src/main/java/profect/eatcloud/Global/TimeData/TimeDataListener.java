package profect.eatcloud.Global.TimeData;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import profect.eatcloud.Security.SecurityUtil;
import java.time.Instant;
import java.util.UUID;

public class TimeDataListener {



	@PrePersist
	public void prePersist(BaseTimeEntity entity) {
		if (entity.getTimeData() == null) {
			String user = SecurityUtil.getCurrentUsername();
			Instant now = Instant.now();

			TimeData td = TimeData.builder()
				.pTimeId(
					UUID.randomUUID())
				.createdAt(now)
				.createdBy(user)
				.updatedAt(now)
				.updatedBy(user)
				.build();

			entity.setTimeData(td);
		}
	}

	@PreUpdate
	public void preUpdate(BaseTimeEntity entity) {
		if (entity.getTimeData() != null) {
			String user = SecurityUtil.getCurrentUsername();
			Instant now = Instant.now();

			entity.getTimeData().setUpdatedAt(now);
			entity.getTimeData().setUpdatedBy(user);
		}
	}
}