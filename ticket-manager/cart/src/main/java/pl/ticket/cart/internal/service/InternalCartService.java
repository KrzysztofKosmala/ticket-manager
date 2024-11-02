package pl.ticket.cart.internal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.cart.common.model.Cart;
import pl.ticket.cart.internal.repository.InternalCartRepository;

@Service
@RequiredArgsConstructor
public class InternalCartService
{
    private final InternalCartRepository internalCartRepository;
    @Transactional
    public void deleteCartById(Long id)
    {
        internalCartRepository.deleteCartById(id);
    }

    public Cart getCart(Long id)
    {
        return internalCartRepository.findById(id).orElseThrow();
    }
}
