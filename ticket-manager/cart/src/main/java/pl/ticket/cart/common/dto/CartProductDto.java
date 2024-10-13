package pl.ticket.cart.common.dto;

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
