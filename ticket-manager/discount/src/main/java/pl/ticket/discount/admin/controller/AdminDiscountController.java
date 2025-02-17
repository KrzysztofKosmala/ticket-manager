package pl.ticket.discount.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ticket.discount.admin.dto.AdminDiscountCreateDto;
import pl.ticket.discount.admin.dto.AdminDiscountDto;
import pl.ticket.discount.admin.service.AdminDiscountService;

@RestController
@RequestMapping("api/v1/admin/discounts")
@RequiredArgsConstructor
public class AdminDiscountController
{
    private final AdminDiscountService discountService;

    @PostMapping
    public AdminDiscountDto createDiscount(@RequestBody @Valid AdminDiscountCreateDto discountDto) {
        return discountService.createDiscount(discountDto);
    }

    @PatchMapping("/{id}/deactivate")
    public AdminDiscountDto  deactivateDiscount(@PathVariable Long id) {
        return discountService.deactivateDiscount(id);
    }
}
