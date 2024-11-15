package pl.ticket.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConcreteTicketDto
{
    Long id;
    byte[] qrCode;
    Long generalTicketId;
}
