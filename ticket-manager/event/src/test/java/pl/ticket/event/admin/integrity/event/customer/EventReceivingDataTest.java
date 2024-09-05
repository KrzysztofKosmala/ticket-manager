package pl.ticket.event.admin.integrity.event.customer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import pl.ticket.event.admin.event.data_provider.EventProvider;
import pl.ticket.event.admin.event.data_provider.TicketProvider;
import pl.ticket.event.admin.integrity.event.PrePost;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.model.dto.EventDateTimeDto;
import pl.ticket.event.customer.event.repository.EventRepository;
import pl.ticket.event.customer.event.service.EventMapper;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.event.customer.event_occurrence.repository.EventOccurrenceRepository;
import pl.ticket.event.customer.ticket.model.Ticket;
import pl.ticket.event.customer.ticket.repository.TicketRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Sql(scripts={"classpath:test-data-v2.sql"})
@Transactional
public class EventReceivingDataTest extends PrePost {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventOccurrenceRepository eventOccurrenceRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventProvider eventProvider = new EventProvider();
    private TicketProvider ticketProvider = new TicketProvider();

    @Test
    public void shouldGetEventWithOccurrences() {
        Event event = eventRepository.findByIdWithOccurrences(222L).get();
        assertNotNull(event);

        List<EventOccurrence> occurrences = event.getOccurrences();

        assertEquals(50, event.getCapacity(), "Wrong capacity after getting event");
        assertEquals("Workshop B", event.getTitle(), "Wrong tittle after getting event");
        assertEquals("Interactive workshop.", event.getDescription(), "Wrong description after getting event");
        assertEquals("workshop-b", event.getSlug(), "Wrong slug after getting event");
        assertEquals(222, event.getCategoryId(), "Wrong categoryId after getting event");
        assertEquals(1, occurrences.size(), "Unexpected count of occurrences for event");
    }

    @Test
    public void shouldGetEventByDate() {
        Pageable pageable = PageRequest.of(0, 5);
        LocalDate date = LocalDate.of(2024, 8, 19);

        List<EventDateTimeDto> testData = eventProvider.prepareEventDateTimeDtoData();

        List<Event> events = eventRepository.findByDatePaged(date, pageable);
        List<EventDateTimeDto> eventDateTimeDtos = eventMapper.mapToListEventDateTimeDto(events, date);

        assertEquals(2, events.size());
        assertEquals(2, eventDateTimeDtos.size());

        validateEventDateTime(testData, eventDateTimeDtos);
    }

    @Test
    public void shouldGetAllEvents() {
        Pageable pageable = PageRequest.of(0, 3);

        List<Event> allEvents = eventRepository.findAllEventsPaged(pageable);

        assertEquals(3, allEvents.size());
    }

    @Test
    public void shouldGetEventWithTicket() {
        Long eventId = 111L;
        LocalTime timeParsed = LocalTime.parse("18:00");
        LocalDate date = LocalDate.of(2024, 8, 26);

        EventOccurrence eventOccurrence = eventOccurrenceRepository.findEventOccurrenceByEventIdAndTime(eventId, timeParsed, date);

        assertNotNull(eventOccurrence);
        assertEquals(111L, eventOccurrence.getEventId());
        assertEquals(date, eventOccurrence.getDate());
        assertEquals(timeParsed, eventOccurrence.getTime());
        assertEquals(100, eventOccurrence.getSpaceLeft());

        List<Ticket> ticketsForOccurrence = ticketRepository.findTicketsOccurrenceId(eventOccurrence.getId());

        assertEquals(2, ticketsForOccurrence.size());
        assertFalse(ticketsForOccurrence.isEmpty());

        validateEventTicket(ticketsForOccurrence);
    }

    private void validateEventDateTime(List<EventDateTimeDto> expectedData, List<EventDateTimeDto> eventDateTimeDtos ){
        EventDateTimeDto expected = null;
        EventDateTimeDto received = null;

        for(int index = 0; index < expectedData.size(); index++){
            expected = expectedData.get(index);
            received = eventDateTimeDtos.get(index);

            assertEquals(expected.getTitle(), received.getTitle());
            assertEquals(expected.getDate(), received.getDate());
            assertEquals(expected.getDescription(), received.getDescription());
            assertEquals(expected.getTimes().size(), received.getTimes().size());
        }
        List<LocalTime> expectedTimes = expected.getTimes();
        List<LocalTime> receivedTimes = received.getTimes();
        for(int index = 0; index < expectedTimes.size(); index++){
            assertEquals(expectedTimes.get(index), receivedTimes.get(index));
        }
    }

    private void validateEventTicket(List<Ticket> ticketsForOccurrence){
        List<Ticket> expectedTickets = ticketProvider.prepareTicketWithOccurrence();

        Ticket recievedTicket = null;
        Ticket expectedTicket = null;

        for(int index = 0; index < ticketsForOccurrence.size(); index++){
            recievedTicket = ticketsForOccurrence.get(index);
            expectedTicket = expectedTickets.get(index);

            assertEquals(expectedTicket.getId(), recievedTicket.getId());
            assertEquals(expectedTicket.getAmount(), recievedTicket.getAmount());
            assertEquals(expectedTicket.getType(), recievedTicket.getType());
            assertEquals(expectedTicket.getPrice(), recievedTicket.getPrice());
            assertEquals(expectedTicket.getId(), recievedTicket.getId());
            assertEquals(expectedTicket.getId(), recievedTicket.getId());
        }

        EventOccurrence recievedOccurrence = recievedTicket.getEventOccurrence();
        EventOccurrence expectedOccurrence = expectedTicket.getEventOccurrence();

        assertEquals(expectedOccurrence.getId(), recievedOccurrence.getId());
        assertEquals(expectedOccurrence.getEventId(), recievedOccurrence.getEventId());
        assertEquals(expectedOccurrence.getDate(), recievedOccurrence.getDate());
        assertEquals(expectedOccurrence.getTime(), recievedOccurrence.getTime());
        assertEquals(expectedOccurrence.getSpaceLeft(), recievedOccurrence.getSpaceLeft());
    }

}
