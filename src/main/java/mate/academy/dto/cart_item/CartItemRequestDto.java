package mate.academy.dto.cart_item;

public record CartItemRequestDto(
        Long bookId,
        Integer quantity
) {}
