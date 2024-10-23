package pl.ticket.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRow
{
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal price;
    private Long shipmentId;
}
