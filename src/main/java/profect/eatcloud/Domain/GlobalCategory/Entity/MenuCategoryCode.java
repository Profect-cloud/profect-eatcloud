package profect.eatcloud.Domain.GlobalCategory.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import profect.eatcloud.Global.TimeData.TimeData;

@Entity
@Table(name = "p_menu_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryCode {
    @Id
    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_time_id", nullable = false)
    private TimeData pTime;
} 