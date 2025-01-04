package pl.ticket.event.customer.event.model;

import jakarta.persistence.*;
import lombok.*;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;

import java.util.List;


@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@Table(name = "event")
@AllArgsConstructor
public class Event
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
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId")
    private List<EventOccurrence> occurrences;

}
