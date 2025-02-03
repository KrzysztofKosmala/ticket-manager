package pl.ticket.common.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime placeDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @OneToMany
    @JoinColumn(name = "orderId")
    private List<OrderRow> orderRows;
    private BigDecimal grossValue;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Long paymentId;
    private String userId;
    private String orderHash;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Order {\n");
        sb.append("  id=").append(id).append("\n");
        sb.append("  placeDate=").append(placeDate).append("\n");
        sb.append("  orderStatus=").append(orderStatus).append("\n");
        sb.append("  orderRows=[\n");
        if (orderRows != null) {
            for (OrderRow row : orderRows) {
                sb.append("    ").append(row).append("\n");
            }
        }
        sb.append("  ]\n");
        sb.append("  grossValue=").append(grossValue).append("\n");
        sb.append("  firstname='").append(firstname).append("'\n");
        sb.append("  lastname='").append(lastname).append("'\n");
        sb.append("  email='").append(email).append("'\n");
        sb.append("  phone='").append(phone).append("'\n");
        sb.append("  paymentId=").append(paymentId).append("\n");
        sb.append("  userId='").append(userId).append("'\n");
        sb.append("  orderHash='").append(orderHash).append("'\n");
        sb.append("}");
        return sb.toString();
    }
}
