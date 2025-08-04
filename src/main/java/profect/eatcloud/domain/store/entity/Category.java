package profect.eatcloud.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import profect.eatcloud.global.timeData.BaseTimeEntity;

@Entity
@Table(name = "p_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseTimeEntity {
    @Id
    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public Category(UUID categoryId, Object o) {
        super();
    }

} 