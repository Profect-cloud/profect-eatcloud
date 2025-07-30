package profect.eatcloud.Domain.Store.Entity;

import profect.eatcloud.Global.TimeData.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.BatchSize;

import java.util.UUID;

@Entity
@Table(name = "p_ai_responses")
@Getter
@Setter
public class AiResponse extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ai_response_id")
    private UUID aiResponseId;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

}