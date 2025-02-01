package pl.ticket.event.admin.ticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.dto.ConcreteTicketDto;
import pl.ticket.dto.ConcreteTicketDtoList;
import pl.ticket.event.admin.ticket.model.AdminConcreteTicket;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.repository.AdminConcreteTicketRepository;
import pl.ticket.event.configuration.rabbit.RabbitMqConfig;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminConcreteTicketService
{
    private final AdminConcreteTicketRepository adminConcreteTicketRepository;
    private final Clock clock;
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqConfig rabbitMqConfig;

    public void processDeletingConcreteTickets(LocalDate date, AdminTicket ticket)
    {
        LocalDate now = LocalDate.now(clock);

        List<AdminConcreteTicket> concreteTickets = adminConcreteTicketRepository.findByGeneralTicketId(ticket.getId());
        if(date.isAfter(now) && !concreteTickets.isEmpty())// jeszcze prawdzic godzine
        {
                       //na kolejke do zwrotu kasy
            List<ConcreteTicketDto> list = concreteTickets.stream()
                    .map(adminConcreteTicket ->
                        ConcreteTicketDto.builder()
                                .id(adminConcreteTicket.getId())
                                .generalTicketId(adminConcreteTicket.getGeneralTicket().getId())
                                .build())
                    .toList();

            ConcreteTicketDtoList concreteTicketDtoList = ConcreteTicketDtoList.builder()
                    .concreteTicketDtoList(list)
                    .build();

            rabbitMqMessageProducer.publish
                    (
                            concreteTicketDtoList,
                            rabbitMqConfig.getInternalExchange(),
                            rabbitMqConfig.getRefoundPaymentRoutingKey()
                    );

            //mail z przeprosinami

        }
        adminConcreteTicketRepository.deleteAllById(concreteTickets.stream().map(AdminConcreteTicket::getId).collect(Collectors.toList()));
    }
}
