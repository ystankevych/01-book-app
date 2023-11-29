package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.CreateOrderRequestDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.UpdateOrderDto;
import mate.academy.dto.order.UpdateOrderResponseDto;
import mate.academy.dto.orderitem.OrderItemDto;
import mate.academy.model.User;
import mate.academy.service.OrderService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing Orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Creating an order",
            description = """
                    Creating an order for a logged-in user, 
                    parameters to be specified: 'shipping address'"""
    )
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeOrder(Authentication authentication,
                               @RequestBody @Valid CreateOrderRequestDto orderDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(user.getId(), orderDto);
    }

    @Operation(
            summary = "Get order history",
            description = """
                    Get order history of a logged-in user 
                    with default pagination (10 books per page) and sorting"""
    )
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<OrderDto> getAll(Authentication authentication,
                                 @ParameterObject @PageableDefault Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrders(user.getId(), pageable);
    }

    @Operation(
            summary = "Update order status",
            description = """
                    Update order status. Parameters to be specified: order id and status
                    (COMPLETED, DELIVERED, PENDING, SHIPPED)"""
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UpdateOrderResponseDto updateOrderStatus(@PathVariable @Positive Long id,
                                                    @RequestBody @Valid UpdateOrderDto orderDto) {
        return orderService.updateOrderStatus(id, orderDto);
    }

    @Operation(
            summary = "Get list of order items",
            description = """
                    Get list of order items of a logged-in user. 
                    Parameters to be specified: order id"""
    )
    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    public List<OrderItemDto> findOrderItemsByOrder(@PathVariable @Positive Long orderId,
                                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.findOrderItemsByOrder(user.getId(), orderId);
    }

    @Operation(
            summary = "Get order item by its id",
            description = """
                    Get a specific order item from the order of a logged-in user"""
    )
    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public OrderItemDto findOrderItemById(@PathVariable @Positive Long orderId,
                                          @PathVariable @Positive Long itemId,
                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.findOrderItemById(orderId, itemId, user.getId());
    }
}
