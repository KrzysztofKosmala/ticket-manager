package pl.ticket.dto;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CartSummaryDto {\n");
        sb.append("  id=").append(id).append("\n");
        sb.append("  items=[\n");
        if (items != null) {
            for (CartSummaryItemDto item : items) {
                sb.append("    ").append(item).append("\n");
            }
        }
        sb.append("  ]\n");
        sb.append("  summary=").append(summary).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
