package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.CreateOrderRequestDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.exception.OrderException;
import mate.academy.mapper.OrderMapper;
import mate.academy.model.Order;
import mate.academy.model.ShoppingCart;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.OrderRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    @Override
    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequestDto orderDto) {
        ShoppingCart cart = cartRepository.findByUserId(userId);
        if (cart.getCartItems().isEmpty()) {
            throw new OrderException("Cart is empty for user: " + userId);
        }
        Order order = orderMapper.cartToOrder(cart, orderDto.shippingAddress());
        cartItemRepository.deleteAllByShoppingCart_Id(cart.getId());
        return orderMapper.toOrderDto(orderRepository.save(order));
    }
}
