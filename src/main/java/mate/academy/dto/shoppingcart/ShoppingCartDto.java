package mate.academy.dto.shoppingcart;

import java.util.List;
import mate.academy.dto.cartitem.CartItemDto;

public record ShoppingCartDto(
        Long id,
        Long userId,
        List<CartItemDto> cartItems) {
}
