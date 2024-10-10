package pl.ticket.customer.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.customer.model.Order;
import pl.ticket.customer.model.OrderRow;
import pl.ticket.customer.model.Payment;
import pl.ticket.customer.model.dto.OrderDto;
import pl.ticket.customer.model.dto.OrderSummary;
import pl.ticket.customer.repository.OrderRepository;
import pl.ticket.customer.repository.OrderRowRepository;
import pl.ticket.customer.repository.PaymentRepository;
import pl.ticket.customer.service.mapper.OrderMapper;
import pl.ticket.dto.OrderCreatedEvent;
import pl.ticket.feign.cart.CartClient;
import pl.ticket.dto.CartSummaryDto;
import pl.ticket.feign.event.EventClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService
{
    private final PaymentRepository paymentRepository;
    private final CartClient cartClient;
    private final EventClient eventClient;
    private final OrderRepository orderRepository;
    private final SagaOrderProcessService sagaOrderProcessService;
    private final OrderRowRepository orderRowRepository;

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

        Order order = OrderMapper.createNewOrder(orderDto, cart, payment, userId);

        orderRepository.save(order);

        List<OrderRow> orderRows = saveProductRows(cart, order.getId());

        order.setOrderRows(orderRows);


        OrderCreatedEvent orderCreatedEvent = OrderMapper.toOrderCreatedEvent(order);
        //odjąć z puli dostpenych(SAGA paattern) - prawdopodobnie trzeba usunac space left z occurance i zastąpić to isCommonPool
            //leci request przez feighn do ticket controllera i tam jest updatowany amount w zależoności czy occurance do ktorego przypisanny jest ticket ma wspólny pool czy nie updatuje amount tylko w jednym tickecie lub we wszysich przypisanych do tego occurance
        //zapisać nowy order
        sagaOrderProcessService.orderCreated(orderCreatedEvent);
        //mail z potwierdzeniem
        //wyczyścić koszyk
        //save order rows
        return null;
    }


    private List<OrderRow> saveProductRows(CartSummaryDto cart, Long orderId) {
        return cart.getItems().stream()
                .map(cartItem -> OrderMapper.mapToOrderRowWithQuantity(orderId, cartItem)
                )
                .peek(orderRowRepository::save)
                .toList();
    }


}
