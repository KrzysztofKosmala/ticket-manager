package pl.ticket.feign.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CartSummaryDto
{
    private Long id;
    private List<CartSummaryItemDto> items;
    private SummaryDto summary;
}
