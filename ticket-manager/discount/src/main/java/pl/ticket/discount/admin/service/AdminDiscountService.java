package pl.ticket.discount.admin.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.discount.admin.AdminDiscountMapper;
import pl.ticket.discount.admin.dto.AdminDiscountCreateDto;
import pl.ticket.discount.admin.dto.AdminDiscountDto;
import pl.ticket.discount.admin.model.AdminDiscount;
import pl.ticket.discount.admin.repossitory.AdminDiscountRepository;

@Service
@RequiredArgsConstructor
public class AdminDiscountService
{
    private final AdminDiscountRepository discountRepository;
    private final AdminDiscountMapper discountMapper;


    public AdminDiscountDto createDiscount(AdminDiscountCreateDto discountDto)
    {
        AdminDiscount discount = discountMapper.toEntity(discountDto);
        discountRepository.save(discount);
        return discountMapper.toAdminDto(discount);
    }

    public AdminDiscountDto deactivateDiscount(Long id) {
        AdminDiscount discount = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found"));

        discount.setIsActive(false);
        discountRepository.save(discount);

        return discountMapper.toAdminDto(discount);
    }
}
