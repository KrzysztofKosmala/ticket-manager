package pl.ticket.event.admin.event.data_provider;


import pl.ticket.event.customer.event.model.dto.EventDateTimeDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventProvider {

    public List<EventDateTimeDto> prepareEventDateTimeDtoData(){
        LocalDate date = LocalDate.of(2024, 8, 19);

        EventDateTimeDto eventDto1 = EventDateTimeDto.builder()
                .title("Concert A")
                .description("Live concert event.")
                .date(date)
                .times(List.of(LocalTime.parse("18:00")))
                .build();
        EventDateTimeDto eventDto2 = EventDateTimeDto.builder()
                .title("Opera A")
                .description("Musical event.")
                .date(date)
                .times(List.of(LocalTime.parse("10:00")))
                .build();

        return List.of(eventDto1, eventDto2);
    }

}
