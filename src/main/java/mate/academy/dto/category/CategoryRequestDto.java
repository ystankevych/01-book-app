package mate.academy.dto.category;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(
        @NotBlank(message = "Category name must not be null or empty")
        @Size(max = 32, message = "Maximum allowed size 32 characters")
        String name,
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String description
) {}
