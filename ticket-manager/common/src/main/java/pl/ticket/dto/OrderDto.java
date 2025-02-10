package pl.ticket.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderDto
{
    private Long id;
    private LocalDateTime placeDate;
    private String orderStatus;
    private List<OrderRowDto> orderRows;
    private BigDecimal grossValue;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Long paymentId;
    private String userId;
    private String orderHash;
}
