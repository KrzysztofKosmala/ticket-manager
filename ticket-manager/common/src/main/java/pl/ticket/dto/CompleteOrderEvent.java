package pl.ticket.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CompleteOrderEvent
{
    private Long orderId;
    private String message;
    private String clientEmail;
    List<ConcreteTicketDto> concreteTickets;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CompleteOrderEvent {\n");
        sb.append("  orderId=").append(orderId).append("\n");
        sb.append("  message='").append(message).append("'\n");
        sb.append("  clientEmail='").append(clientEmail).append("'\n");
        sb.append("  concreteTickets=[\n");
        if (concreteTickets != null) {
            for (ConcreteTicketDto ticket : concreteTickets) {
                sb.append("    ").append(ticket).append("\n");
            }
        }
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }
}
