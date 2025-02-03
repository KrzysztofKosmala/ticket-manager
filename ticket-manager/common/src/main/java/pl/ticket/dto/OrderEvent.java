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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Order {\n");
        sb.append("  order id=").append(orderId).append("\n");
        sb.append("  message=").append(message).append("\n");
        sb.append("  client email=").append(clientEmail).append("\n");
        sb.append("  orderRows=[\n");
        if (orderRows != null) {
            for (OrderRowDto row : orderRows) {
                sb.append("    ").append(row).append("\n");
            }
        }
        sb.append("  ]\n");
        return sb.toString();
    }
}
