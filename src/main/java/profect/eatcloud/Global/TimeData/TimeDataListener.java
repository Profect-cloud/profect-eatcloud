package profect.eatcloud.Global.TimeData;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import profect.eatcloud.Security.SecurityUtil;

public class TimeDataListener {

	@PrePersist
	public void prePersist(BaseTimeEntity entity) {
		String user = SecurityUtil.getCurrentUsername();
		TimeData td = BeanUtil.getBean(TimeDataService.class).createTimeData(user);
		entity.setTimeData(td);
	}

	@PreUpdate
	public void preUpdate(BaseTimeEntity entity) {
		String user = SecurityUtil.getCurrentUsername();
		TimeData td = BeanUtil.getBean(TimeDataService.class)
			.updateTimeData(entity.getTimeData().getPTimeId(), user);
		entity.setTimeData(td);
	}
}