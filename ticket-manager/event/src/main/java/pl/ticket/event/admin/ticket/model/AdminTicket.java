package pl.ticket.event.admin.ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;

import java.math.BigDecimal;


@Data
@Builder
@Entity
@Table(name = "ticket")
@NoArgsConstructor
@AllArgsConstructor
public class AdminTicket
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
    // Relacja ManyToOne z AdminEventOccurrence
    @ManyToOne
    @JoinColumn(name = "event_occurrence_id", nullable = false)
    private AdminEventOccurrence eventOccurrence;

    // Relacja ManyToOne z AdminEvent
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private AdminEvent event;
    @Enumerated(EnumType.STRING)
    private AdminTicketType type;
    private BigDecimal price;
    private int amount;
}
