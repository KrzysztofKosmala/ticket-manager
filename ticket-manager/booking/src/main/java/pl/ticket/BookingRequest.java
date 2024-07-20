package pl.ticket;

import java.util.List;

public record BookingRequest(List<TicketRequest> ticketRequests) {
}
