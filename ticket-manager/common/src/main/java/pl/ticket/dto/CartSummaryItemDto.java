package pl.ticket.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "id")
public class CartSummaryItemDto
{
    private Long id;
    private int quantity;
    private ProductDto product;
    private BigDecimal lineValue;
}
