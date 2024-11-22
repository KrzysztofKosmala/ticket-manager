package pl.ticket.event.internal.ticket.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderRowDto;
import pl.ticket.dto.QrCodeDto;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.event.internal.ticket.exception.QrCastingException;
import pl.ticket.event.internal.ticket.exception.TicketValidationException;
import pl.ticket.event.internal.ticket.model.InternalConcreteTicket;
import pl.ticket.event.internal.ticket.model.InternalTicket;
import pl.ticket.event.internal.ticket.repository.InternalConcreteTicketRepository;
import pl.ticket.event.internal.ticket.repository.InternalTicketRepository;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
@Slf4j
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
                EventOccurrence eventOccurrence = internalConcreteTicket.getGeneralTicket().getEventOccurrence();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                QrCodeDto qrCodeDto = QrCodeDto.builder()
                        .concreteTicketId(internalConcreteTicket.getId())
                        .isUsed(internalConcreteTicket.getIsUsed())
                        .eventId(eventOccurrence.getEventId())
                        .date(eventOccurrence.getDate().format(dateFormatter))
                        .time(eventOccurrence.getTime().format(timeFormatter))
                        .eventOccurrenceId(eventOccurrence.getId())
                        .build();
            try {
                internalConcreteTicket.setQrCode(qrGenerator.generateQRCodeFromObject(qrCodeDto));
            } catch (Exception e) {
                throw new QrCastingException("Problem z geenerowaniem Qr Kodu.");
            }

        });

        return internalConcreteTickets;
    }

    public boolean validateConcreteTicketByQrCode(byte[] base64QrCode)
    {
        //TODO: dodać handling exceptions
       QrCodeDto qrCodeDto;
        InternalConcreteTicket internalConcreteTicket;
        try {
            qrCodeDto = qrGenerator.decodeQRCode(base64QrCode, QrCodeDto.class);
            internalConcreteTicket = internalConcreteTicketRepository.findById(qrCodeDto.getConcreteTicketId())
                    .orElseThrow(() -> new TicketValidationException("Nie można znaleźć konkretnego biletu używając danych z QR kodu: "));
        } catch (Exception e) {
            throw new TicketValidationException("Wystąpił problem przy dekodowaniu QR kodu.");
        }

        if
        (
                        !qrCodeDto.getIsUsed() &&
                        qrCodeDto.getConcreteTicketId().equals(internalConcreteTicket.getId()) &&
                        qrCodeDto.getEventOccurrenceId().equals(internalConcreteTicket.getGeneralTicket().getEventOccurrence().getId()) &&
                        qrCodeDto.getEventId().equals(internalConcreteTicket.getGeneralTicket().getEventOccurrence().getEventId())
        ){
            internalConcreteTicket.setIsUsed(true);
            internalConcreteTicketRepository.save(internalConcreteTicket);
            return true;
        }else{
            return false;
        }
    }
}
