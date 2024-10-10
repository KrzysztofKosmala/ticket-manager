package pl.ticket.customer.model;

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
    @OneToOne
    private Payment payment;
    private String userId;
    private String orderHash;
}
