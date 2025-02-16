package pl.ticket.event.admin.ticket.dto;

import pl.ticket.event.admin.ticket.model.AdminTicketType;

import java.math.BigDecimal;
import java.util.List;


public record AdminTicketInitUpdateDto(AdminTicketType type, List<BigDecimal> prices)
{
}
