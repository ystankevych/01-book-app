package mate.academy.dto.order;


import mate.academy.dto.orderitem.OrderItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long userId,
        List<OrderItemDto> orderItems,
        String orderDate,
        BigDecimal total,
        String status
) {}
