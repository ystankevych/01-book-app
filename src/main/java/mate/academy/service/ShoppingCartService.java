package mate.academy.service;

import mate.academy.dto.cart_item.CartItemDto;
import mate.academy.dto.cart_item.CartItemRequestDto;
import mate.academy.dto.shopping_cart.ShoppingCartDto;
import mate.academy.model.User;

public interface ShoppingCartService {
    void registerShoppingCart(User user);
    ShoppingCartDto getCartByUserId(Long id);
    CartItemDto addBookToCart(Long userId, CartItemRequestDto item);
}
