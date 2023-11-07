package mate.academy.service;

import mate.academy.dto.order.CreateOrderRequestDto;
import mate.academy.dto.order.OrderDto;

public interface OrderService {
    OrderDto createOrder(Long userId, CreateOrderRequestDto orderDto);
}
