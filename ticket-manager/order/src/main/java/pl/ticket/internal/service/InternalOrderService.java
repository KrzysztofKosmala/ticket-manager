package pl.ticket.internal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import pl.ticket.common.SagaOrderProcessService;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderStatus;
import pl.ticket.dto.*;
import pl.ticket.email.EmailClient;
import pl.ticket.feign.cart.CartClient;
import pl.ticket.feign.event.EventClient;
import pl.ticket.internal.repository.InternalOrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternalOrderService
{

    private final SagaOrderProcessService sagaOrderProcessService;
    private final InternalOrderRepository internalOrderRepository;

    @Transactional
    public void changeStatusToReserved(OrderEvent orderEvent)
    {
        //TODO: jak tu gdzieś będzie problem to trzeba wszystko wycofać znowu
        Order order = internalOrderRepository.findOrderById(orderEvent.getOrderId());

        order.setOrderStatus(OrderStatus.RESERVED);

        sagaOrderProcessService.publishOrderReserved(orderEvent);
    }

    @Transactional
    public void changeStatusToCanceled(OrderEvent orderEvent)
    {
        Order order = internalOrderRepository.findOrderById(orderEvent.getOrderId());
        //TODO:do notification

        order.setOrderStatus(OrderStatus.CANCELED);
    }

    @Transactional
    public void changeStatusToPaid(OrderEvent orderEvent)
    {
        Order order = internalOrderRepository.findOrderById(orderEvent.getOrderId());
        sagaOrderProcessService.publishToPrepareConcreteTickets(orderEvent);
        order.setOrderStatus(OrderStatus.PAID);
    }

    @Transactional
    public void unbookOrder(OrderEvent orderEvent)
    {
        Order order = internalOrderRepository.findOrderById(orderEvent.getOrderId());
        order.setOrderStatus(OrderStatus.CANCELLING);

        sagaOrderProcessService.publishToUnbookOrder(orderEvent);
    }

    @Transactional
    public void cancelOrder(OrderEvent orderEvent)
    {
        Order order = internalOrderRepository.findOrderById(orderEvent.getOrderId());
        //TODO:do notification
        order.setOrderStatus(OrderStatus.CANCELED);
    }

    @Transactional
    public void changeStatusToCompleted(CompleteOrderEvent orderEvent)
    {
        Order order = internalOrderRepository.findOrderById(orderEvent.getOrderId());
        order.setOrderStatus(OrderStatus.COMPLETED);
    }
}
