package profect.eatcloud.Domain.GlobalCategory.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import java.util.UUID;

@Entity
@Table(name = "delivery_areas")
@Getter @Setter
public class DeliveryArea extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "area_id")
	private UUID areaId;

	@Column(name = "area_name", nullable = false, length = 100)
	private String areaName;
}