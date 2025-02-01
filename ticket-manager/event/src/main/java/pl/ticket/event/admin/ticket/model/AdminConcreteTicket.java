package pl.ticket.event.admin.ticket.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@Entity
@Table(name = "concrete_ticket")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminConcreteTicket
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
    private AdminTicket generalTicket;
    private Boolean isUsed;

    @Column(name = "qr_code")
    private byte[] qrCode;

}