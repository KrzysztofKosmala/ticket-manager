package pl.ticket.booking;

public record TicketRequest
        (
         Integer eventId,
         String attendee
    )
{
}
