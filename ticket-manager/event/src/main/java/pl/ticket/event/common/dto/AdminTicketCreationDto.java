package pl.ticket.event.common.dto;

import pl.ticket.event.admin.ticket.model.AdminTicketType;

import java.math.BigDecimal;

public record AdminTicketCreationDto(AdminTicketType type, BigDecimal price, int amount) {
}
