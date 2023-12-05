package mate.academy.dto.category;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
        @NotBlank(message = "Category name must not be null or empty")
        @Max(255)
        String name,
        @Max(255)
        String description
) {
}
