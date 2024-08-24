package pl.ticket.event.admin.event_occurrence.model;

import jakarta.persistence.*;
import lombok.*;
import pl.ticket.event.admin.ticket.model.AdminTicket;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Table(name = "event_occurrence")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class AdminEventOccurrence
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long eventId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(name = "space_left", nullable = false)
    private Integer spaceLeft;

    @OneToMany(mappedBy = "eventOccurrence", fetch = FetchType.EAGER)
    private List<AdminTicket> tickets;
}
