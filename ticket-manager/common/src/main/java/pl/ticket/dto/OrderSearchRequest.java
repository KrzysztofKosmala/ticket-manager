package pl.ticket.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderSearchRequest(
        Filters filters,
        Sort sort,
        Integer limit,
        Integer offset,
        Boolean includeRows
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Filters(
            Long orderId,
            List<String> statuses,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            BigDecimal minGrossValue,
            BigDecimal maxGrossValue
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Sort(
            String field,
            String direction
    ) {}
}
