package mate.academy.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequestDto(
        @NotBlank(message = "Shipping address must not be null or empty")
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String shippingAddress
) {}
