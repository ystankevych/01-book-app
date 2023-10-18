package mate.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateBookRequestDto(
        @NotBlank(message = "Title must not be null or empty")
        String title,
        @NotBlank(message = "Author must not be null or empty")
        String author,
        @NotBlank(message = "Isbn must not be null or empty")
        @Size(min = 10, max = 13, message = "Required length 10-13 digits")
        String isbn,
        @NotNull(message = "Price must not be null")
        @Positive(message = "Price must not be negative")
        String title,
        String author,
        String isbn,
        BigDecimal price,
        String description,
        String coverImage
) {}
