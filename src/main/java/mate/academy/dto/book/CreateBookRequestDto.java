package mate.academy.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

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
        BigDecimal price,
        String description,
        String coverImage,
        List<Long> categoriesId
) {}
