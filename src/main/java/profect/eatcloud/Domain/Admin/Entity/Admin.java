package profect.eatcloud.Domain.Admin.Entity;

<<<<<<< HEAD
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
=======
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import profect.eatcloud.Global.TimeData.TimeData;
>>>>>>> d974436be3d6aa74dec64244dac7a084c0036739

@Entity
@Table(name = "p_admins")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
<<<<<<< HEAD
public class Admin extends BaseTimeEntity implements CustomUserDetails {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 20)
	private String username;

	@Column(nullable = false)
	private String email;
=======
public class Admin {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(nullable = false)
    private String password;
>>>>>>> d974436be3d6aa74dec64244dac7a084c0036739

	@Column(nullable = false)
	private String password;

	@Column(name = "phone_number", length = 18)
	private String phoneNumber;

<<<<<<< HEAD
	@Column(length = 50)
	private String position;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
=======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_time_id", nullable = false)
    private TimeData pTime;
>>>>>>> d974436be3d6aa74dec64244dac7a084c0036739
}
