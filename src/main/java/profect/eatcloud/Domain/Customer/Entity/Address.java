package profect.eatcloud.Domain.Customer.Entity;

import jakarta.persistence.*;
import lombok.*;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import java.util.UUID;

@Entity
@Table(name = "p_addresses")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "zipcode", length = 10)
	private String zipcode;

	@Column(name = "road_addr", length = 500)
	private String roadAddr;

	@Column(name = "detail_addr", length = 200)
	private String detailAddr;

	@Column(name = "is_selected")
	private Boolean isSelected = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	public void updateAddress(String zipcode, String roadAddr, String detailAddr) {
		this.zipcode = zipcode;
		this.roadAddr = roadAddr;
		this.detailAddr = detailAddr;
	}

	public void changeSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}