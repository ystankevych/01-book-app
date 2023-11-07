package mate.academy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.CreateOrderRequestDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.model.User;
import mate.academy.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public OrderDto placeOrder(Authentication authentication,
                               @RequestBody @Valid CreateOrderRequestDto orderDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(user.getId(), orderDto);
    }
}
