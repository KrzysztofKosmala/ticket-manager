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
    private BigDecimal price;
    private Long shipmentId;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Order {\n");
        sb.append("  id=").append(id).append("\n");
        sb.append("  productId=").append(productId).append("\n");
        sb.append("  productName=").append(productName).append("\n");
        sb.append("  description=").append(description).append("\n");
        sb.append("  price=").append(price).append("\n");
        sb.append("  shipmentId=").append(shipmentId).append("\n");

        sb.append("  }\n");
        return sb.toString();
    }
}
