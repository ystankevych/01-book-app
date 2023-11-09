package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.CreateOrderRequestDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.UpdateOrderDto;
import mate.academy.dto.order.UpdateOrderResponseDto;
import mate.academy.dto.orderitem.OrderItemDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.OrderException;
import mate.academy.mapper.OrderItemMapper;
import mate.academy.mapper.OrderMapper;
import mate.academy.model.Order;
import mate.academy.model.Order.Status;
import mate.academy.model.OrderItem;
import mate.academy.model.ShoppingCart;
import mate.academy.repository.OrderItemRepository;
import mate.academy.repository.OrderRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequestDto orderDto) {
        ShoppingCart cart = cartRepository.findByUserId(userId);
        if (cart.getCartItems().isEmpty()) {
            throw new OrderException("Cart is empty for user: " + userId);
        }
        Order order = orderMapper.cartToOrder(cart, orderDto.shippingAddress());
        cart.clearCart();
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public List<OrderDto> getAllOrders(Long userId, Pageable pageable) {
        return orderMapper.toOrderDtoList(orderRepository.findAllByUserId(userId, pageable));
    }

    @Override
    public UpdateOrderResponseDto updateOrderStatus(Long orderId, UpdateOrderDto orderDto) {
        Order order = orderRepository.findById(orderId)
                .map(o -> {
                    o.setStatus(getStatusByName(orderDto.status()));
                    return o;
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with id: %d not found", orderId)
                ));
        return orderMapper.toUpdateDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemDto> findOrderItemsByOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with id: %d not found for user: %d", orderId, userId)
                ));
        return orderItemMapper.toOrderItemDtoList(order.getOrderItems());
    }

    @Override
    public OrderItemDto findOrderItemById(Long orderId, Long orderItemId, Long userId) {
        OrderItem item = itemRepository.findByIdAndOrderIdAndUserId(orderItemId, orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order item: %d not found in order: %d for user: %d",
                                orderItemId, orderId, userId)
                ));
        return orderItemMapper.toOrderItemDto(item);
    }

    private Status getStatusByName(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(
                    String.format("Status: %s not found", status), e
            );
        }
    }
}
