package pl.ticket.customer.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import pl.ticket.customer.model.Payment;
import pl.ticket.customer.model.dto.OrderDto;
import pl.ticket.customer.model.dto.OrderSummary;
import pl.ticket.customer.repository.OrderRepository;
import pl.ticket.customer.repository.PaymentRepository;
import pl.ticket.feign.cart.CartClient;
import pl.ticket.feign.cart.CartSummaryDto;
import pl.ticket.feign.event.EventClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService
{
    private final PaymentRepository paymentRepository;
    private final CartClient cartClient;
    private final EventClient eventClient;
    private final OrderRepository orderRepository;


    @Transactional
    public OrderSummary placeOrder(OrderDto orderDto, String userId)
    {

        Long cartId = orderDto.getCartId();
        //wyciagnac koszyk
        CartSummaryDto cart = cartClient.getCart(cartId);
        //sprawdź dostępność

        //wyciagnac payment
        Payment payment = paymentRepository.findById(orderDto.getPaymentId()).orElseThrow();
        //poprawić payment w changelogu

        //odjąć z puli dostpenych(SAGA paattern) - prawdopodobnie trzeba usunac space left z occurance i zastąpić to isCommonPool
            //leci request przez feighn do ticket controllera i tam jest updatowany amount w zależoności czy occurance do ktorego przypisanny jest ticket ma wspólny pool czy nie updatuje amount tylko w jednym tickecie lub we wszysich przypisanych do tego occurance
        //zapisać nowy order
        //mail z potwierdzeniem
        //wyczyścić koszyk
        //save order rows


        int i =1;

        return null;
    }
}
