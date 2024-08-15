package pl.ticket.admin.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event.service.AdminEventService;
import pl.ticket.event.admin.event_occurrence.repository.AdminEventOccurrenceRepository;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOcurrenceService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AdminEventServiceTest {

    @Mock
    private AdminEventRepository adminEventRepository;
    @Mock
    private AdminEventOccurrenceRepository eventOccurrenceRepository;


    @InjectMocks
    private AdminEventOcurrenceService adminEventOcurrenceService;
    @InjectMocks
    private AdminEventService adminEventService;


    @Test
    void shouldCreateCategory(){
//
//        AdminEvent event = AdminEvent.builder()
//                .id(7L)
//                .title("Event 1")
//                .description("Desc Event 1")
//                .slug("ev1")
//                .capacity(100)
//                .categoryId(1)
//                .build();
//
//
//        AdminEventOccurrence adminEventOccurrence = new AdminEventOccurrence();
//        adminEventOccurrence.setDate(LocalDate.now());
//        adminEventOccurrence.setTime(LocalTime.now());
//        adminEventOccurrence.setSpaceLeft(20);
//        adminEventOccurrence.setEventId(event.getId());
//
//        when(eventOccurrenceRepository.save(any())).thenReturn(adminEventOccurrence);
//
//        AdminEventOccurrence savedEventOccurrence = adminEventOcurrenceService.addEventOccurrence(adminEventOccurrence);
//        assertThat(savedEventOccurrence).isNotNull();
//        assertThat(savedEventOccurrence.getEventId()).isEqualTo(7L);
//        assertThat(savedEventOccurrence.getDate()).isEqualTo(LocalDate.now());
//        assertThat(savedEventOccurrence.getSpaceLeft()).isEqualTo(20);
    }

}
