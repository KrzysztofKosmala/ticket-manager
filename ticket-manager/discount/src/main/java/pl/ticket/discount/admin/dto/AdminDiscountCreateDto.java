package pl.ticket.discount.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.ticket.discount.common.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;


public record AdminDiscountCreateDto(
        @NotBlank String code,

        @NotNull DiscountType discountType,

        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal value,

        @DecimalMin(value = "0.0", inclusive = false) BigDecimal minOrderValue,

        @DecimalMin(value = "0.0", inclusive = true) BigDecimal maxDiscount,

        @NotNull LocalDateTime validFrom,

        @NotNull LocalDateTime validTo,

        @NotNull @Min(1) Integer usageLimit,

        @NotNull Boolean isActive,

        Long userId
) {}
