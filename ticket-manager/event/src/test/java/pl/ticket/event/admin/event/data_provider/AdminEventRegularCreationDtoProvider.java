package pl.ticket.event.admin.event.data_provider;

import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.dto.EventType;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceRegularCreationDto;
import pl.ticket.event.admin.ticket.model.AdminTicketType;
import pl.ticket.event.common.dto.AdminTicketCreationDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AdminEventRegularCreationDtoProvider
{

    public AdminEventRegularCreationDto correct()
    {
        AdminEventRegularCreationDto dto1 = new AdminEventRegularCreationDto();

        dto1.setEventType(EventType.REGULAR);
        dto1.setTitle("Event 1");
        dto1.setDescription("Description 1");
        dto1.setCapacity(100);
        dto1.setSlug("event-1");
        dto1.setCategoryId(1L);
        dto1.setIsCommonTicketPool(true);
        dto1.setStartDate(LocalDate.of(2024, 8,19));
        dto1.setEndDate(LocalDate.of(2024, 9,2));

        dto1.setOccurrences(List.of
                (
                        new AdminEventOccurrenceRegularCreationDto(LocalTime.of(10,0), "sobota"),
                        new AdminEventOccurrenceRegularCreationDto(LocalTime.of(11,0), "sobota"),
                        new AdminEventOccurrenceRegularCreationDto(LocalTime.of(9,0), "środa")
                ));

        dto1.setTickets(List.of
                (
                        new AdminTicketCreationDto(AdminTicketType.FULL_PRICE, BigDecimal.valueOf(25), 0),
                        new AdminTicketCreationDto(AdminTicketType.HALF_PRICE, BigDecimal.valueOf(12), 0)
                ));

        return dto1;

    }

    public AdminEventRegularCreationDto startDateIsBeforeNow()
    {
        AdminEventRegularCreationDto dto1 = new AdminEventRegularCreationDto();
        dto1.setEventType(EventType.REGULAR);
        dto1.setTitle("Event 1");
        dto1.setDescription("Description 1");
        dto1.setCapacity(100);
        dto1.setSlug("event-1");
        dto1.setCategoryId(1L);
        dto1.setIsCommonTicketPool(true);
        dto1.setStartDate(LocalDate.of(2023, 10,9));
        dto1.setEndDate(LocalDate.of(2024, 9, 2));


        return dto1;

    }

    public AdminEventRegularCreationDto endDateIsBeforeNow()
    {
        AdminEventRegularCreationDto dto1 = new AdminEventRegularCreationDto();
        dto1.setEventType(EventType.REGULAR);
        dto1.setTitle("Event 1");
        dto1.setDescription("Description 1");
        dto1.setCapacity(100);
        dto1.setSlug("event-1");
        dto1.setCategoryId(1L);
        dto1.setIsCommonTicketPool(true);
        dto1.setStartDate(LocalDate.of(2023, 10,9));
        dto1.setEndDate(LocalDate.of(2023, 11, 2));


        return dto1;

    }


    public AdminEventRegularCreationDto endDateIsBeforeStartDate()
    {
        AdminEventRegularCreationDto dto1 = new AdminEventRegularCreationDto();
        dto1.setEventType(EventType.REGULAR);
        dto1.setTitle("Event 1");
        dto1.setDescription("Description 1");
        dto1.setCapacity(100);
        dto1.setSlug("event-1");
        dto1.setCategoryId(1L);
        dto1.setIsCommonTicketPool(true);
        dto1.setStartDate(LocalDate.of(2024, 10,9));
        dto1.setEndDate(LocalDate.of(2024, 9, 2));


        return dto1;

    }

    public AdminEventRegularCreationDto wrongEventType()
    {
        AdminEventRegularCreationDto dto1 = new AdminEventRegularCreationDto();
        dto1.setEventType(EventType.OCCASIONAL);

        return dto1;

    }
}
