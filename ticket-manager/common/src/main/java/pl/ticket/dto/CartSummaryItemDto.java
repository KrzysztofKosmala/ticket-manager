package pl.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
