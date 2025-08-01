package profect.eatcloud.Domain.GlobalCategory.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import profect.eatcloud.Global.TimeData.TimeData;

@Entity
@Table(name = "order_type_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTypeCode extends BaseTimeEntity {
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