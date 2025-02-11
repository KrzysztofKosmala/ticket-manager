package pl.ticket.common.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRow
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Long shipmentId;

    @Override
    public String toString() {
        return "OrderRow {\n" +
                "  id=" + id + "\n" +
                "  orderId=" + orderId + "\n" +
                "  productId=" + productId + "\n" +
                "  productName='" + productName + "'\n" +
                "  description='" + description + "'\n" +
                "  price=" + price + "\n" +
                "  shipmentId=" + shipmentId + "\n" +
                '}';
    }
}
