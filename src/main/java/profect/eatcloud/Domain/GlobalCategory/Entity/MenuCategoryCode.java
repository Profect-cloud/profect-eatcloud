package profect.eatcloud.domain.globalCategory.entity;

import jakarta.persistence.*;
import lombok.*;

import profect.eatcloud.Global.TimeData.BaseTimeEntity;

@Entity
@Table(name = "p_menu_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryCode extends BaseTimeEntity {
    @Id
    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
} 