package profect.eatcloud.Global.TimeData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(TimeDataListener.class)
@Getter @Setter
public abstract class BaseTimeEntity {
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "p_time_id", nullable = false)
	private TimeData timeData;
}