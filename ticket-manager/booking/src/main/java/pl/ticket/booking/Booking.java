package pl.ticket.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Booking
{
    @Id
    @SequenceGenerator
            (
                    name = "booking_id_sequence",
                    sequenceName = "booking_id_sequence"
            )
    @GeneratedValue
            (
                    strategy = GenerationType.SEQUENCE,
                    generator = "booking_id_sequence"
            )
    private Integer id;

    private Integer customerId;

    private Integer eventId;

    private String attendee;
}
