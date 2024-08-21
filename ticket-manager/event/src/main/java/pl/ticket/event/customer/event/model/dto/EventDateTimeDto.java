package pl.ticket.event.customer.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDateTimeDto {
    private String title;
    private String description;
    private LocalDate date;
    private List<LocalTime> times = new ArrayList<>();
}
