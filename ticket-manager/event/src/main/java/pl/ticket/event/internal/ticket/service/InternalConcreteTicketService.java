package pl.ticket.event.internal.ticket.service;

import com.google.zxing.WriterException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.dto.ConcreteTicketDto;
import pl.ticket.dto.OrderRowDto;
import pl.ticket.event.internal.ticket.model.InternalConcreteTicket;
import pl.ticket.event.internal.ticket.model.InternalTicket;
import pl.ticket.event.internal.ticket.repository.InternalConcreteTicketRepository;
import pl.ticket.event.internal.ticket.repository.InternalTicketRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InternalConcreteTicketService
{
    private final InternalConcreteTicketRepository internalConcreteTicketRepository;
    private final InternalTicketRepository internalTicketRepository;
    private final QrGenerator qrGenerator;

    @Transactional
    public List<InternalConcreteTicket> createConcreteTickets(List<OrderRowDto> orderRows)
    {
        List<InternalConcreteTicket> concreteTickets = new ArrayList<>();
        for(OrderRowDto orderRow: orderRows)
        {
            InternalTicket internalTicket = internalTicketRepository.findById(orderRow.getProductId()).orElseThrow();
            for (int i=0; i<orderRow.getQuantity(); i++)
            {
                InternalConcreteTicket internalConcreteTicket = InternalConcreteTicket.builder()
                        .generalTicket(internalTicket)
                        .isUsed(false)
                        .build();
                concreteTickets.add(internalConcreteTicket);
            }
        }
        List<InternalConcreteTicket> internalConcreteTickets = internalConcreteTicketRepository.saveAll(concreteTickets);

        internalConcreteTickets.forEach(internalConcreteTicket -> {
            try {
                internalConcreteTicket.setQrCode(qrGenerator.generateQRCode(internalConcreteTicket.getId().toString(), 200,200));
            } catch (WriterException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return internalConcreteTickets;
    }
}
