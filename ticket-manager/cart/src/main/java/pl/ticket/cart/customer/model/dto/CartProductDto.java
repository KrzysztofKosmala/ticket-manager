package pl.ticket.cart.customer.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDto
{
    private int quantity;
    private ProductDto productDto;
}
