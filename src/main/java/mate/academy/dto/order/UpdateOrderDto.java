package mate.academy.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOrderDto(
        @NotBlank(message = "Status must not be null or empty")
        @Size(max = 32, message = "Maximum allowed size 32 characters")
        String status
) {}
