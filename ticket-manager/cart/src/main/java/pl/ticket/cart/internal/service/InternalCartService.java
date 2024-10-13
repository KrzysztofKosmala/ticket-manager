package pl.ticket.cart.internal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
}
