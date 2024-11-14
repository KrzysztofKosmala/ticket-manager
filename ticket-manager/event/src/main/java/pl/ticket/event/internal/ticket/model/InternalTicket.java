package pl.ticket.event.internal.ticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.event.customer.ticket.model.TicketType;
import pl.ticket.event.internal.event.model.InternalEvent;

import java.math.BigDecimal;

@Data
@Builder
@Entity
@Table(name = "ticket")
@NoArgsConstructor
@AllArgsConstructor
public class InternalTicket
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
    private Long id;
    @Enumerated(EnumType.STRING)
    private TicketType type;
    private BigDecimal price;
    private int amount;

    @ManyToOne
    @JoinColumn(name = "event_occurrence_id", nullable = false)
    private EventOccurrence eventOccurrence;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private InternalEvent event;
    @Version
    private Long version;
}
