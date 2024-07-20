package pl.ticket;

public record TicketRequest
        (
         Integer eventId,
         String attendee
    )
{
}
