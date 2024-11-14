package pl.ticket.event.internal.ticket.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.dto.ConcreteTicketDto;
import pl.ticket.dto.OrderRowDto;
import pl.ticket.dto.TicketWithDetailsDto;
import pl.ticket.event.internal.ticket.model.InternalConcreteTicket;
import pl.ticket.event.internal.ticket.model.InternalTicket;
import pl.ticket.event.internal.ticket.repository.InternalConcreteTicketRepository;
import pl.ticket.event.internal.ticket.repository.InternalTicketRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InternalConcreteTicketService
{
    private final InternalConcreteTicketRepository internalConcreteTicketRepository;
    private final InternalTicketRepository internalTicketRepository;

    @Transactional
    public List<ConcreteTicketDto> createConcreteTickets(List<OrderRowDto> orderRows)
    {
        List<InternalConcreteTicket> concreteTickets = new ArrayList<>();
        for(OrderRowDto orderRow: orderRows)
        {
            InternalTicket internalTicket = internalTicketRepository.findById(orderRow.getProductId()).orElseThrow();
            for (int i=0; i<orderRow.getQuantity(); i++)
            {
                InternalConcreteTicket internalConcreteTicket = InternalConcreteTicket.builder()
                        .generalTicketId(internalTicket)
                        .qrCode(new byte[0])
                        .build();
                concreteTickets.add(internalConcreteTicket);
            }
        }
        internalConcreteTicketRepository.saveAll(concreteTickets);

        return concreteTickets.stream().map(concreteTicket ->
                        ConcreteTicketDto.builder()
                                .id(concreteTicket.getId())
                                .qrCode(concreteTicket.getQrCode())
                                .generalTicketId(concreteTicket.getGeneralTicketId().getId())
                                .build())
                .toList();
    }
}
