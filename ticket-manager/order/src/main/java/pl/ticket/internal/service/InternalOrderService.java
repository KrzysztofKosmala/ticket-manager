package pl.ticket.internal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import pl.ticket.common.SagaOrderProcessService;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderStatus;
import pl.ticket.dto.OrderEvent;
import pl.ticket.internal.repository.InternalOrderRepository;

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
        //jak tu gdzieś będzie problem to trzeba wszystko wycofać znowu
        Order order = internalOrderRepository.findOrderById(orderEvent.getOrderId());

        order.setOrderStatus(OrderStatus.RESERVED);

        //TODO:do notification

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
    public void changeStatusToCompleted(OrderEvent orderEvent)
    {
        Order order = internalOrderRepository.findOrderById(orderEvent.getOrderId());
        //TODO:do notification with tickets/products hash (QR code?) może endpoint w tickecie do generowania biletów wysyłanych na maila
        order.setOrderStatus(OrderStatus.COMPLETED);
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
}
