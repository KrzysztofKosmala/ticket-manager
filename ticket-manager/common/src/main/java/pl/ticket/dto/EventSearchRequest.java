package pl.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventSearchRequest(
        Filters filters,
        Sort sort,
        Integer limit,
        Integer offset
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Filters(
            String query,
            LocalDate dateFrom,
            LocalDate dateTo,
            Long categoryId,
            Boolean onlyAvailable
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Sort(
            String field,
            String direction
    ) {}
}
