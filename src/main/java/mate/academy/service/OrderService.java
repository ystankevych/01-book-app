package mate.academy.service;

import java.util.List;
import mate.academy.dto.order.CreateOrderRequestDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.UpdateOrderDto;
import mate.academy.dto.order.UpdateOrderResponseDto;
import mate.academy.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(Long userId, CreateOrderRequestDto orderDto);

    List<OrderDto> getAllOrders(Long userId, Pageable pageable);

    UpdateOrderResponseDto updateOrderStatus(Long orderId, UpdateOrderDto orderDto);

    List<OrderItemDto> findOrderItemsByOrder(Long userId, Long orderId);

    OrderItemDto findOrderItemById(Long orderId, Long orderItemId, Long userId);
}
