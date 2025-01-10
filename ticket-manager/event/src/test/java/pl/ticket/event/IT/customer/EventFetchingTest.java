package pl.ticket.event.IT.customer;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.ticket.dto.EventDto;
import pl.ticket.event.customer.event.service.EventService;
import pl.ticket.event.IT.PrePost;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@Transactional
public class EventFetchingTest extends PrePost {

    @Autowired
    private EventService underTest;


    @Test
    void shouldGetEventDetailsById() {

        // When
        EventDto result = underTest.getEventDetailsById(1L);

        // Then
        assertEquals(4, result.getOccurrences().size());
    }

    @Test
    public void shouldGetEventByDate() {
        EventDto toCheck = underTest.getEventDetailsByIdAndDate(1L, LocalDate.parse("2024-10-21"));


        assertEquals(2, toCheck.getOccurrences().size());
        assertEquals("2024-10-21", toCheck.getOccurrences().get(0).getDate().toString());
        assertEquals("2024-10-21", toCheck.getOccurrences().get(1).getDate().toString());

        assertEquals("Dziady", toCheck.getTitle());


    }
}
