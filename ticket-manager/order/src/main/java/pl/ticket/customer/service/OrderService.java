package pl.ticket.customer.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.common.SagaOrderProcessService;
import pl.ticket.common.mapper.EmailMessageGenerator;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderRow;
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

        Long cartId = orderDto.getCartId();

        CartSummaryDto cart = cartClient.getCart(cartId);

        Order order = OrderMapper.createNewOrder(orderDto, cart, userId);

        orderRepository.save(order);

        //TODO: można to zatąpić hashmapą
        List<Long> cartItemIds = cart.getItems().stream().map(cartItem -> cartItem.getProduct().getId()).toList();
        List<TicketWithDetailsDto> ticketsWithDetailsByTicketIds = eventClient.getTicketsWithDetailsByTicketIds(cartItemIds);

        List<OrderRow> orderRows = saveProductRows(cart, order.getId(), ticketsWithDetailsByTicketIds);

        order.setOrderRows(orderRows);

        OrderEvent orderEvent = OrderMapper.toOrderEvent(order);

        sagaOrderProcessService.publishOrderCreated(orderEvent);
        //clearOrderCart(orderDto);
        return OrderMapper.createOrderSummary(order, "to be implemented");
    }

    private List<OrderRow> saveProductRows(CartSummaryDto cart, Long orderId, List<TicketWithDetailsDto> ticketsWithDetailsByTicketIds) {

        return ticketsWithDetailsByTicketIds.stream().map(ticket ->
                {
                    CartSummaryItemDto itemDto = cart.getItems().stream().filter(item -> item.getProduct().getId().equals(ticket.getId())).findFirst().get();

                    return OrderMapper.toOrderRow(orderId, itemDto, ticket);
                }
        ).peek(orderRowRepository::save).toList();

    }

    private void clearOrderCart(OrderDto orderDto) {
        cartClient.deleteItemsByCartId(orderDto.getCartId());
        cartClient.deleteCart(orderDto.getCartId());
    }


}
