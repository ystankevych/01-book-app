package mate.academy.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(
        @Positive(message = "Book id must be positive")
        @NotNull(message = "Book id must not be null")
        Long bookId,
        @Positive(message = "Quantity must be positive")
        int quantity) {

}
