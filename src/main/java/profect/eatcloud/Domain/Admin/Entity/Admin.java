package profect.eatcloud.Domain.Admin.Entity;

import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import jakarta.persistence.*;


import profect.eatcloud.Global.TimeData.TimeData;

@Entity
@Table(name = "p_admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(nullable = false)
    private String password;

	@Column(name = "phone_number", length = 18)
	private String phoneNumber;

	@Column(length = 50)
	private String position;
}
