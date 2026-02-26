package pl.ticket.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderSearchResponse(
        List<OrderSummary> items,
        long totalCount,
        boolean hasMore
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OrderSummary(
            Long id,
            LocalDateTime placeDate,
            String status,
            BigDecimal grossValue,
            Long paymentId,
            List<OrderRowSummary> rows
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OrderRowSummary(
            Long productId,
            String productName,
            String description,
            BigDecimal price,
            Long shipmentId
    ) {}
}
