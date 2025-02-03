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
public class ProductDto
{
    private Long id;
    private BigDecimal price;

    @Override
    public String toString() {
        return "\nProductDto {\n" +
                "  id=" + id + "\n" +
                "  price=" + price + "\n" +
                '}';
    }
}
