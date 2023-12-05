package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.UpdateCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.model.User;
import mate.academy.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
@Validated
public class ShoppingCartController {
    private final ShoppingCartService cartService;

    @Operation(summary = "Get the shopping cart of a logged-in user")
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return cartService.getUsersCart(user.getId());
    }

    @Operation(
            summary = "Add a book to the shopping cart",
            description = """
                    Add a new book/update the quantity( if the book is already in the card,
                    the amount will be added up) of books in the cart of a logged in user"""
    )
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto addBook(Authentication authentication,
                                   @RequestBody @Valid CartItemRequestDto cartItem) {
        User user = (User) authentication.getPrincipal();
        return cartService.addBookToCart(user.getId(), cartItem);
    }

    @Operation(
            summary = "Update the number of books in the cart",
            description = """
                    Update the number of books in the cart of a logged-in user.
                    Parameters to be specified: cart item id and new quantity""")
    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto updateBookQuantity(Authentication authentication,
                                              @PathVariable @Positive Long cartItemId,
                                              @RequestBody @Valid UpdateCartItemDto item) {
        User user = (User) authentication.getPrincipal();
        return cartService.updateBookQuantity(user.getId(), cartItemId, item);
    }

    @Operation(
            summary = "Delete book from the cart of a logged-in user",
            description = """
                    Delete book from the cart of a logged-in user.
                    Parameter to be specified: cart item id"""
    )
    @DeleteMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookFromCart(Authentication authentication,
                                   @PathVariable @Positive Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        cartService.deleteBookFromCart(user.getId(), cartItemId);
    }
}
