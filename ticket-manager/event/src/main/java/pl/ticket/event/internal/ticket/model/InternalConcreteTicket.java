package pl.ticket.event.internal.ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "concrete_ticket")
@NoArgsConstructor
@AllArgsConstructor
public class InternalConcreteTicket
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

    @ManyToOne
    @JoinColumn(name = "general_ticket_id")
    private InternalTicket generalTicketId;
    private Boolean isUsed;
    @Lob
    private byte[] qrCode;

}
