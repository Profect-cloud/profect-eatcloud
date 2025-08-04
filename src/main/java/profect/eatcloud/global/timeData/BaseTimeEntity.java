package profect.eatcloud.global.timeData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(TimeDataListener.class)
@Getter @Setter
public abstract class BaseTimeEntity {
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
	@JoinColumn(name = "p_time_id", nullable = false)
	private TimeData timeData;
}