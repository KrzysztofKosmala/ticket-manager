package pl.ticket.discount.admin;

import org.springframework.stereotype.Component;
import pl.ticket.discount.admin.dto.AdminDiscountCreateDto;
import pl.ticket.discount.admin.dto.AdminDiscountDto;
import pl.ticket.discount.admin.model.AdminDiscount;

import java.time.LocalDateTime;

@Component
public class AdminDiscountMapper
{
    public AdminDiscountDto toAdminDto(AdminDiscount discount) {
        return new AdminDiscountDto(
                discount.getId(),
                discount.getCode(),
                discount.getDiscountType(),
                discount.getValue(),
                discount.getMinOrderValue(),
                discount.getMaxDiscount(),
                discount.getValidFrom(),
                discount.getValidTo(),
                discount.getUsageLimit(),
                discount.getUsedCount(),
                discount.getIsActive(),
                discount.getUserId(),
                discount.getCreatedAt(),
                discount.getUpdatedAt()
        );
    }

    public AdminDiscount toEntity(AdminDiscountCreateDto dto) {
        return AdminDiscount.builder()
                .code(dto.code())
                .discountType(dto.discountType())
                .value(dto.value())
                .minOrderValue(dto.minOrderValue())
                .maxDiscount(dto.maxDiscount())
                .validFrom(dto.validFrom())
                .validTo(dto.validTo())
                .usageLimit(dto.usageLimit() != null ? dto.usageLimit() : 0)
                .usedCount(0)
                .isActive(dto.isActive() != null ? dto.isActive() : true)
                .userId(dto.userId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

