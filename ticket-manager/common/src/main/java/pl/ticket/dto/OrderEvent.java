package pl.ticket.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderEvent
{
    private Long orderId;
    private List<OrderRowDto> orderRows;
}
