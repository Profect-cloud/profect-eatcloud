package profect.eatcloud.Domain.Customer.Entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import profect.eatcloud.Global.TimeData.TimeData;
import profect.eatcloud.Security.userDetails.CustomUserDetails;

@Entity
@Table(name = "p_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer implements CustomUserDetails {

	@Id
	private UUID id;

	@Column(name = "name", nullable = false, unique = true, length = 20)
	private String username;

	@Column(length = 100)
	private String nickname;

	@Column(length = 255)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(name = "phone_number", length = 18)
	private String phoneNumber;

	private Integer points;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_time_id", nullable = false)
	private TimeData pTime;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
	}
}
