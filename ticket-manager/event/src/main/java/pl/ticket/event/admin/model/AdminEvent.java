package pl.ticket.event.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "event")
@NoArgsConstructor
@AllArgsConstructor
public class AdminEvent
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
    private Integer id;
    private Integer capacity;
    private String title;
    private String description;
    private String slug;
}