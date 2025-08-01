package profect.eatcloud.Domain.Admin.Entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import profect.eatcloud.Global.TimeData.BaseTimeEntity;

@Entity
@Table(name = "p_manager_store_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerStoreApplication extends BaseTimeEntity {

	@Id
	@Column(name = "application_id", nullable = false)
	private UUID applicationId;

	// Manager 신청 정보
	@Column(name = "manager_name", length = 20, nullable = false)
	private String managerName;

	@Column(name = "manager_email", length = 255, nullable = false)
	private String managerEmail;

	@Column(name = "manager_password", length = 255, nullable = false)
	private String managerPassword;

	@Column(name = "manager_phone_number", length = 18)
	private String managerPhoneNumber;

	// Store 신청 정보
	@Column(name = "store_name", length = 200, nullable = false)
	private String storeName;

	@Column(name = "store_address", length = 300)
	private String storeAddress;

	@Column(name = "store_phone_number", length = 18)
	private String storePhoneNumber;

	@Column(name = "category_id")
	private UUID categoryId;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	// 심사 상태
	@Column(name = "status", length = 20, nullable = false)
	private String status;

	@Column(name = "reviewer_admin_id")
	private UUID reviewerAdminId;

	@Column(name = "review_comment", columnDefinition = "TEXT")
	private String reviewComment;
}