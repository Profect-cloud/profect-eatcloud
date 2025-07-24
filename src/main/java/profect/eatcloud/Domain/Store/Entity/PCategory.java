package profect.eatcloud.Domain.Store.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PCategory {
    @Id
    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;
} 