package pl.ticket;

import java.math.BigDecimal;

public record TicketCreationRequest(Integer eventId, TicketType type, BigDecimal price, int amount) {
}
