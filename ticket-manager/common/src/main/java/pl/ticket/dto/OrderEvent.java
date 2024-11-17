package pl.ticket.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class OrderEvent
{
    private Long orderId;
    private String message;
    private String clientEmail;
    private List<OrderRowDto> orderRows;
}
