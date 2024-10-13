package pl.ticket.cart.internal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.cart.internal.repository.InternalCartItemRepository;

@Service
@RequiredArgsConstructor
public class InternalCartItemService
{

    private final InternalCartItemRepository cartItemRepository;
    @Transactional
    public void deleteItemsByCartId(Long cartId)
    {
        cartItemRepository.deleteByCartId(cartId);
    }
}
