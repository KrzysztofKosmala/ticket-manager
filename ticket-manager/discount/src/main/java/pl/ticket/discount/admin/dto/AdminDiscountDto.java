package pl.ticket.discount.admin.dto;

import pl.ticket.discount.common.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminDiscountDto(
        Long id,
        String code,
        DiscountType discountType,
        BigDecimal value,
        BigDecimal minOrderValue,
        BigDecimal maxDiscount,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        Integer usageLimit,
        Integer usedCount,
        Boolean isActive,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

