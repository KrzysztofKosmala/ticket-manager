package pl.ticket.event.customer.ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;

import java.math.BigDecimal;


@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Ticket
{
    @Id
    @SequenceGenerator
            (
                    name = "ticket_id_sequence",
                    sequenceName = "ticket_id_sequence"
            )
    @GeneratedValue
            (
                    strategy = GenerationType.SEQUENCE,
                    generator = "ticket_id_sequence"
            )
    private Integer id;
    @Enumerated(EnumType.STRING)
    private TicketType type;
    private BigDecimal price;
    private int amount;

    @ManyToOne
    @JoinColumn(name = "event_occurrence_id", nullable = false)
    private EventOccurrence eventOccurrence;

}
