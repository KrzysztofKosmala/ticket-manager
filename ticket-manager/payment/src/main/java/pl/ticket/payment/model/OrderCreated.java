package pl.ticket.payment.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreated {
    private Long orderId;

    private LocalDateTime placeDate;
    private OrderStatus orderStatus;
    private PaymentType paymentType;
    private List<OrderRow> orderRows;
    private BigDecimal grossValue;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Long userId;
    private String orderHash;
}
