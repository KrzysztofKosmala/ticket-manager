package pl.ticket.event.admin.integrity.event.customer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import pl.ticket.event.admin.integrity.PrePost;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.repository.EventRepository;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class EventReceivingDataTest extends PrePost {
    @Autowired
    private EventRepository eventRepository;

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
}
