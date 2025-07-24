package profect.eatcloud.Domain.Global.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_time")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Time {
    @Id
    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;
} 