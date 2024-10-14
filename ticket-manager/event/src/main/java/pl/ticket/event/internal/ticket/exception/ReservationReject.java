package pl.ticket.event.internal.ticket.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReservationReject
{
    private final String message;
}
