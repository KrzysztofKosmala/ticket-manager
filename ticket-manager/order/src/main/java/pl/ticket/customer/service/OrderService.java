package pl.ticket.customer.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.common.SagaOrderProcessService;
import pl.ticket.common.mapper.EmailMessageGenerator;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderRow;
import pl.ticket.common.model.OrderStatus;
import pl.ticket.common.model.dto.OrderDto;
import pl.ticket.common.model.dto.OrderSummary;
import pl.ticket.customer.repository.OrderRepository;
import pl.ticket.customer.repository.OrderRowRepository;
import pl.ticket.common.mapper.OrderMapper;
import pl.ticket.dto.CartSummaryItemDto;
import pl.ticket.dto.OrderEvent;
import pl.ticket.dto.TicketWithDetailsDto;
import pl.ticket.email.EmailClient;
import pl.ticket.feign.cart.CartClient;
import pl.ticket.dto.CartSummaryDto;
import pl.ticket.feign.event.EventClient;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService
{
    //TODO: refactor reserve to book
    private final CartClient cartClient;
    private final EventClient eventClient;
    private final OrderRepository orderRepository;
    private final SagaOrderProcessService sagaOrderProcessService;
    private final OrderRowRepository orderRowRepository;
    private final EmailClient emailClient;


    @Transactional
    public OrderSummary placeOrder(OrderDto orderDto, String userId)
    {
        log.trace("New order request from user: {} Order: {}", userId, orderDto.toString());
        Long cartId = orderDto.getCartId();

        CartSummaryDto cart = cartClient.getCart(cartId);

        log.trace("Fetched cart from CART SERVICE: {}", cart.toString());

        Order order = OrderMapper.createNewOrder(orderDto, cart, userId);

        orderRepository.save(order);


        List<Long> cartItemIds = cart.getItems().stream().map(cartItem -> cartItem.getProduct().getId()).toList();
        List<TicketWithDetailsDto> ticketsWithDetailsByTicketIds = eventClient.getTicketsWithDetailsByTicketIds(cartItemIds);

        List<OrderRow> orderRows = saveProductRows(cart, order.getId(), ticketsWithDetailsByTicketIds);

        order.setOrderRows(orderRows);
        log.trace("Saved order: {}", order.toString());

        OrderEvent orderEvent = OrderMapper.toOrderEvent(order);

        sagaOrderProcessService.publishOrderCreated(orderEvent);
        clearOrderCart(orderDto);
        return OrderMapper.createOrderSummary(order, "/api/v1/orders/" + order.getId() + "/status");
    }

    private List<OrderRow> saveProductRows(CartSummaryDto cart, Long orderId, List<TicketWithDetailsDto> ticketsWithDetailsByTicketIds) {


        List<OrderRow> orderRows = ticketsWithDetailsByTicketIds.stream().map(ticket ->
                {
                    CartSummaryItemDto itemDto = cart.getItems().stream().filter(item -> item.getProduct().getId().equals(ticket.getId())).findFirst().get();

                    return OrderMapper.toOrderRow(orderId, itemDto, ticket);
                }
        ).flatMap(Collection::stream).toList();

        return orderRowRepository.saveAll(orderRows);

    }

    private void clearOrderCart(OrderDto orderDto) {
        log.trace("Deleting cart from CART SERVICE: {}", orderDto.getCartId());
        cartClient.deleteItemsByCartId(orderDto.getCartId());
        cartClient.deleteCart(orderDto.getCartId());
    }


    public OrderStatus getStatus(Long orderId)
    {
        return orderRepository.findOrderStatusById(orderId);
    }
}
