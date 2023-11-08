package mate.academy.dto.order;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.dto.orderitem.OrderItemDto;

public record OrderDto(
        Long id,
        Long userId,
        List<OrderItemDto> orderItems,
        String orderDate,
        BigDecimal total,
        String status
) {}
