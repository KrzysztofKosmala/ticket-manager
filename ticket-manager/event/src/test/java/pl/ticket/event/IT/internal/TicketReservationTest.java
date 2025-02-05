package pl.ticket.event.IT.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.ticket.dto.OrderEvent;
import pl.ticket.dto.OrderRowDto;
import pl.ticket.event.IT.PrePost;
import pl.ticket.event.data_provider.OrderEventDataProvider;
import pl.ticket.event.internal.ticket.model.InternalTicket;
import pl.ticket.event.internal.ticket.repository.InternalTicketRepository;
import pl.ticket.event.internal.ticket.service.InternalTicketService;
import pl.ticket.event.internal.ticket.service.SagaReservationProcessService;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TicketReservationTest extends PrePost
{


    @MockBean
    private SagaReservationProcessService sagaReservationProcessService;
    @Autowired
    private InternalTicketService underTest;
    @Autowired
    private InternalTicketRepository internalTicketRepository;

    /*TODO: wszystkie casy*/

    @Test
    void shouldReserveTicketsSuccessfully()
    {
        doNothing().when(sagaReservationProcessService).publishReservationCompleted(any(OrderEvent.class));
        OrderEvent randomOrderEvent = OrderEventDataProvider.correct();

        List<Long> rowIds = randomOrderEvent.getOrderRows().stream().map(OrderRowDto::getProductId).toList();

        List<InternalTicket> ticketsBefore = internalTicketRepository.findAllById(rowIds);

        underTest.reserveTickets(randomOrderEvent);

        List<InternalTicket> ticketsAfter = internalTicketRepository.findAllById(rowIds);

        Map<Long, List<OrderRowDto>> rowsGroupedById = randomOrderEvent.getOrderRows().stream().collect(groupingBy(OrderRowDto::getProductId));


        for (Map.Entry<Long, List<OrderRowDto>> entry : rowsGroupedById.entrySet()) {
            Long id = entry.getKey();
            List<OrderRowDto> orderRows = entry.getValue();

            InternalTicket ticketBefore = ticketsBefore.stream().filter(ticket -> ticket.getId().equals(id)).findFirst().get();
            InternalTicket ticketAfter = ticketsAfter.stream().filter(ticket -> ticket.getId().equals(id)).findFirst().get();
            assertEquals(ticketBefore.getAmount() - orderRows.size(), ticketAfter.getAmount(), "Unexpected ticket amount after reservation.");

        }

    }

    @Test
    void shouldUnbookTicketsSuccessfully()
    {
        doNothing().when(sagaReservationProcessService).publishReservationCompleted(any(OrderEvent.class));
        OrderEvent randomOrderEvent = OrderEventDataProvider.correct();

        List<Long> rowIds = randomOrderEvent.getOrderRows().stream().map(OrderRowDto::getProductId).toList();

        List<InternalTicket> ticketsBefore = internalTicketRepository.findAllById(rowIds);

        underTest.unbookTickets(randomOrderEvent);

        List<InternalTicket> ticketsAfter = internalTicketRepository.findAllById(rowIds);

        Map<Long, List<OrderRowDto>> rowsGroupedById = randomOrderEvent.getOrderRows().stream().collect(groupingBy(OrderRowDto::getProductId));


        for (Map.Entry<Long, List<OrderRowDto>> entry : rowsGroupedById.entrySet()) {
            Long id = entry.getKey();
            List<OrderRowDto> orderRows = entry.getValue();

            InternalTicket ticketBefore = ticketsBefore.stream().filter(ticket -> ticket.getId().equals(id)).findFirst().get();
            InternalTicket ticketAfter = ticketsAfter.stream().filter(ticket -> ticket.getId().equals(id)).findFirst().get();
            assertEquals(ticketBefore.getAmount() + orderRows.size(), ticketAfter.getAmount(), "Unexpected ticket amount after reservation.");

        }
    }
}