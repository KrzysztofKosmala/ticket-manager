package pl.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class QrCodeDto
{
    Long concreteTicketId;
    Boolean isUsed;
    Long eventId;
    String date;
    String time;
    Long eventOccurrenceId;
}
