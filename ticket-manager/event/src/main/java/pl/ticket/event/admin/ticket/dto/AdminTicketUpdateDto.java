package pl.ticket.event.admin.ticket.dto;

import pl.ticket.event.admin.ticket.model.AdminTicketType;

import java.math.BigDecimal;

public record AdminTicketUpdateDto(AdminTicketType type, BigDecimal price)
{
}
