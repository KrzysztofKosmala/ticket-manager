package pl.ticket.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CartSummaryItemDto
{
    private Long id;
    private int quantity;
    private ProductDto product;
    private BigDecimal lineValue;
    @Override
    public String toString() {
        return "\nCartSummaryItemDto {\n" +
                "  quantity=" + quantity + "\n" +
                "  product=" + product + "\n" +
                "  lineValue=" + lineValue + "\n" +
                '}';
    }
}
