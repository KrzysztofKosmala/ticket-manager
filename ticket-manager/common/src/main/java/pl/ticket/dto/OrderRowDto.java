package pl.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRowDto
{
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal price;
    private Long shipmentId;
}
