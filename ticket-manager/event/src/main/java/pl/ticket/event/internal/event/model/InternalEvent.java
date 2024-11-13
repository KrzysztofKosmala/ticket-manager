package pl.ticket.event.internal.event.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;

import java.util.List;

@Data
@Builder
@Entity
@Table(name = "event")
@NoArgsConstructor
@AllArgsConstructor
public class InternalEvent
{
    @Id
    @SequenceGenerator
            (
                    name = "event_id_sequence",
                    sequenceName = "event_id_sequence"
            )
    @GeneratedValue
            (
                    strategy = GenerationType.SEQUENCE,
                    generator = "event_id_sequence"
            )
    private Long id;
    private Integer capacity;
    private Long categoryId;
    private String title;
    private String description;
    private String slug;

}
