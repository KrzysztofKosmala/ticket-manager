package pl.ticket.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConcreteTicketDtoList
{
    private List<ConcreteTicketDto> concreteTicketDtoList;
}
