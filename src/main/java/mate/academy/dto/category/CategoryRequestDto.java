package mate.academy.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
        @NotBlank(message = "Category name must not be null or empty")
        String name,
        String description
) {}
