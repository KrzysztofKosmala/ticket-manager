package pl.ticket.cart.customer.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto
{
    private Long id;
    private BigDecimal price;
}
