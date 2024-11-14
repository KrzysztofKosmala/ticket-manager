package pl.ticket.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConcreteTicketDto
{
    Long id;
    byte[] qrCode;
    Long generalTicketId;
}
