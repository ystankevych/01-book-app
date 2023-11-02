package mate.academy.dto.shopping_cart;

import mate.academy.dto.cart_item.CartItemDto;

import java.util.Set;

public record ShoppingCartDto(
        Long id,
        Long userId,
        Set<CartItemDto> cartItems
) {}
