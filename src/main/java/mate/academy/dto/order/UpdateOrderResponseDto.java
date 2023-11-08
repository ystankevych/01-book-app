package mate.academy.dto.order;

public record UpdateOrderResponseDto(
        Long orderId,
        String status
) {}
