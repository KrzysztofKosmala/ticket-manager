package pl.ticket.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ticket.admin.repository.AdminOrderRepository;
import pl.ticket.common.mapper.OrderMapper;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderStatus;
import pl.ticket.customer.repository.OrderRepository;
import pl.ticket.dto.OrderDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderService
{
    private final AdminOrderRepository adminOrderRepository;
    private final OrderRepository orderRepository;

    public Page<OrderDto> getOrders(Pageable pageable)
    {
        Page<Order> orderPage = adminOrderRepository.findAll(pageable);
        List<OrderDto> orderDtoList = orderPage.stream().map(OrderMapper::mapToDto).toList();

        return new PageImpl<>(orderDtoList, pageable, orderDtoList.size());
    }

    public OrderDto getOrderById(Long orderId)
    {
        return OrderMapper.mapToDto(
                adminOrderRepository.findOrderById(orderId)
                        .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found")));

    }

    public OrderDto getOrderByEmail(String email)
    {
        return OrderMapper.mapToDto(adminOrderRepository.findOrderByEmail(email).orElseThrow(() -> new NoSuchElementException("Order with email " + email + " not found")));
    }

    public OrderDto updateOrder(Long orderId, OrderDto orderDto)
    {
        Order existingOrder = adminOrderRepository.findOrderById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));

        existingOrder.setPlaceDate(orderDto.getPlaceDate());
        existingOrder.setOrderStatus(OrderStatus.valueOf(orderDto.getOrderStatus()));
        existingOrder.setGrossValue(orderDto.getGrossValue());
        existingOrder.setFirstname(orderDto.getFirstname());
        existingOrder.setLastname(orderDto.getLastname());
        existingOrder.setEmail(orderDto.getEmail());
        existingOrder.setPhone(orderDto.getPhone());
        existingOrder.setPaymentId(orderDto.getPaymentId());
        existingOrder.setUserId(orderDto.getUserId());
        existingOrder.setOrderHash(orderDto.getOrderHash());
        existingOrder.setOrderRows(orderDto.getOrderRows().stream()
                .map(OrderMapper::mapDtoToOrderRow)
                .collect(Collectors.toList()));

        Order updatedOrder = orderRepository.save(existingOrder);
        return OrderMapper.mapToDto(updatedOrder);
    }
}
