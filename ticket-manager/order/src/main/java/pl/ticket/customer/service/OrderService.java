package pl.ticket.customer.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.common.SagaOrderProcessService;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderRow;
import pl.ticket.common.model.dto.OrderDto;
import pl.ticket.common.model.dto.OrderSummary;
import pl.ticket.customer.repository.OrderRepository;
import pl.ticket.customer.repository.OrderRowRepository;
import pl.ticket.customer.service.mapper.OrderMapper;
import pl.ticket.dto.OrderEvent;
import pl.ticket.feign.cart.CartClient;
import pl.ticket.dto.CartSummaryDto;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService
{
    //TODO: refactor reserve to book
    private final CartClient cartClient;
    private final OrderRepository orderRepository;
    private final SagaOrderProcessService sagaOrderProcessService;
    private final OrderRowRepository orderRowRepository;

    @Transactional
    public OrderSummary placeOrder(OrderDto orderDto, String userId)
    {

        Long cartId = orderDto.getCartId();

        CartSummaryDto cart = cartClient.getCart(cartId);

        Order order = OrderMapper.createNewOrder(orderDto, cart, userId);

        orderRepository.save(order);

        List<OrderRow> orderRows = saveProductRows(cart, order.getId());

        order.setOrderRows(orderRows);

        OrderEvent orderEvent = OrderMapper.toOrderEvent(order);
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


}
