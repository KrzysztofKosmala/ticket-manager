package pl.ticket.customer.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.customer.model.Order;
import pl.ticket.customer.model.OrderRow;
import pl.ticket.customer.model.OrderStatus;
import pl.ticket.customer.model.dto.OrderDto;
import pl.ticket.customer.model.dto.OrderSummary;
import pl.ticket.customer.repository.OrderRepository;
import pl.ticket.customer.repository.OrderRowRepository;
import pl.ticket.customer.service.mapper.OrderMapper;
import pl.ticket.dto.OrderEvent;
import pl.ticket.feign.cart.CartClient;
import pl.ticket.dto.CartSummaryDto;
import pl.ticket.feign.event.EventClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService
{
    //TODO: dodac tu payment
    private final CartClient cartClient;
    private final EventClient eventClient;
    private final OrderRepository orderRepository;
    private final SagaOrderProcessService sagaOrderProcessService;
    private final OrderRowRepository orderRowRepository;

    @Transactional
    public OrderSummary placeOrder(OrderDto orderDto, String userId)
    {

        Long cartId = orderDto.getCartId();

        CartSummaryDto cart = cartClient.getCart(cartId);

        //TODO:pobrac payment z payment service
        //Payment payment = paymentRepository.findById(orderDto.getPaymentId()).orElseThrow();
        Order order = OrderMapper.createNewOrder(orderDto, cart, userId);

        orderRepository.save(order);

        List<OrderRow> orderRows = saveProductRows(cart, order.getId());

        order.setOrderRows(orderRows);


        OrderEvent orderEvent = OrderMapper.toOrderCreatedEvent(order);
        sagaOrderProcessService.publishOrderCreated(orderEvent);
        //TODO:do notification
        clearOrderCart(orderDto);
        return OrderMapper.createOrderSummary(order, "to be implemented");
    }

    private List<OrderRow> saveProductRows(CartSummaryDto cart, Long orderId) {
        return cart.getItems().stream()
                .map(cartItem -> OrderMapper.mapToOrderRowWithQuantity(orderId, cartItem)
                )
                .peek(orderRowRepository::save)
                .toList();
    }

    private void clearOrderCart(OrderDto orderDto) {
        cartClient.deleteItemsByCartId(orderDto.getCartId());
        cartClient.deleteCart(orderDto.getCartId());
    }


    @Transactional
    public void changeStatusToReserved(OrderEvent orderEvent)
    {
        //jak tu gdzieś będzie problem to trzeba wszystko wycofać znowu
        Order order = orderRepository.findOrderById(orderEvent.getOrderId());

        order.setOrderStatus(OrderStatus.RESERVED);

        //TODO:do notification

        sagaOrderProcessService.publishOrderReserved(orderEvent);
    }

    @Transactional
    public void changeStatusToCanceled(OrderEvent orderEvent)
    {
        Order order = orderRepository.findOrderById(orderEvent.getOrderId());
        //TODO:do notification
        order.setOrderStatus(OrderStatus.CANCELED);
    }

    @Transactional
    public void changeStatusToCompleted(OrderEvent orderEvent)
    {
        Order order = orderRepository.findOrderById(orderEvent.getOrderId());
        //TODO:do notification with tickets hash (QR code?)
        order.setOrderStatus(OrderStatus.COMPLETED);
    }
}
