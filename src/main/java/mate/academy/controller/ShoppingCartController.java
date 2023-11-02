package mate.academy.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.cart_item.CartItemDto;
import mate.academy.dto.cart_item.CartItemRequestDto;
import mate.academy.dto.shopping_cart.ShoppingCartDto;
import mate.academy.model.User;
import mate.academy.security.CustomUserDetailsService;
import mate.academy.service.ShoppingCartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
@Validated
public class ShoppingCartController {
    private final CustomUserDetailsService userService;
    private final ShoppingCartService cartService;
    @GetMapping
    public ShoppingCartDto shoppingCart(Authentication authentication) {
        User user = (User) userService.loadUserByUsername(authentication.getName());
        return cartService.getCartByUserId(user.getId());
    }

    @PostMapping
    public CartItemDto addBook(Authentication authentication, @RequestBody CartItemRequestDto cartItem) {
        User user = (User) userService.loadUserByUsername(authentication.getName());
        return cartService.addBookToCart(user.getId(), cartItem);
    }
}
