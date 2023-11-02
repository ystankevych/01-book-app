package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.cart_item.CartItemDto;
import mate.academy.dto.cart_item.CartItemRequestDto;
import mate.academy.dto.shopping_cart.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemRepository itemRepository;
    private final ShoppingCartRepository repository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper cartMapper;
    private final CartItemMapper itemMapper;

    @Override
    public void registerShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        repository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto getCartByUserId(Long id) {
        return cartMapper.toDto(repository.findByUserId(id));
    }

    @Override
    public CartItemDto addBookToCart(Long userId, CartItemRequestDto itemDto) {
        if (!bookRepository.existsById(itemDto.bookId())) {
            throw new EntityNotFoundException(
                    String.format("Book with id: %d is not found", itemDto.bookId()));
        }
        ShoppingCart shoppingCart = repository.findByUserId(userId);
        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(i -> i.getBook().getId().equals(itemDto.bookId()))
                .peek(item -> item.setQuantity(item.getQuantity() + itemDto.quantity()))
                .findFirst()
                .orElseGet(() -> addNewItemToCart(itemDto, shoppingCart));
        repository.save(shoppingCart);
        return itemMapper.toDto(cartItem);
    }

    private CartItem addNewItemToCart(CartItemRequestDto itemDto, ShoppingCart cart) {
        CartItem cartItem = itemMapper.toCartItem(itemDto, cart);
        cart.getCartItems().add(cartItem);
        return cartItem;
    }
}
