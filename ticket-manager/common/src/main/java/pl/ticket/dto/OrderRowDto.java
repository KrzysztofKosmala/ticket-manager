package pl.ticket.dto;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderRowDto
{
    private Long id;
    private Long productId;
    private String productName;
    private String description;
    private int quantity;
    private BigDecimal price;
    private Long shipmentId;
}
