package pl.ticket.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@NoArgsConstructor
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
    private Integer id;
    private String title;
    private String description;
}
