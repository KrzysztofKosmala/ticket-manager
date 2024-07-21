package pl.ticket.booking;

import java.util.List;

public record BookingRequest(List<TicketRequest> ticketRequests) {
}
