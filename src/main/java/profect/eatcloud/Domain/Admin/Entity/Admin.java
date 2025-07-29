package profect.eatcloud.Domain.Admin.Entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import profect.eatcloud.Security.userDetails.CustomUserDetails;

@Entity
@Table(name = "p_admins")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseTimeEntity implements CustomUserDetails {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 20)
	private String username;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(name = "phone_number", length = 18)
	private String phoneNumber;

	@Column(length = 50)
	private String position;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
}
