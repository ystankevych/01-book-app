package mate.academy.service;

import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.UpdateCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.model.User;

public interface ShoppingCartService {
    void registerShoppingCart(User user);

    ShoppingCartDto getUsersCart(Long userId);

    ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto item);

    ShoppingCartDto updateBookQuantity(Long userId, Long cartItemId, UpdateCartItemDto itemDto);

    void deleteBookFromCart(Long userId, Long itemId);

}
