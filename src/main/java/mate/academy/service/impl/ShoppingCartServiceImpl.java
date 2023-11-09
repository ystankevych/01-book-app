package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.UpdateCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemRepository itemRepository;
    private final ShoppingCartRepository repository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper cartMapper;
    private final CartItemMapper itemMapper;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        repository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto getUsersCart(Long userId) {
        return cartMapper.toDto(repository.findByUserId(userId));
    }

    @Transactional
    @Override
    public ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto itemDto) {
        Book book = bookRepository.findById(itemDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Book with id: %d is not found", itemDto.bookId())
                ));
        ShoppingCart cart = repository.findByUserId(userId);
        cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(itemDto.bookId()))
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + itemDto.quantity()),
                        () -> addCartItemToCart(itemDto, book, cart));
        repository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto updateBookQuantity(Long userId,
                                              Long cartItemId, UpdateCartItemDto itemDto) {
        ShoppingCart cart = repository.findByUserId(userId);
        CartItem cartItem = itemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId())
                .map(item -> {
                    item.setQuantity(itemDto.quantity());
                    return item;
                }).orElseThrow(() -> new EntityNotFoundException(
                        String.format("No cart item with id: %d for user: %d", cartItemId, userId)
                ));
        itemRepository.save(cartItem);
        return cartMapper.toDto(cart);
    }

    @Transactional
    @Override
    public void deleteBookFromCart(Long userId, Long itemId) {
        ShoppingCart shoppingCart = repository.findByUserId(userId);
        CartItem cartItem = itemRepository.findByIdAndShoppingCartId(itemId, shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("No such cartItem with id %d in shopping cart", itemId)
                ));
        shoppingCart.removeItemFromCart(cartItem);
    }

    private void addCartItemToCart(CartItemRequestDto itemDto, Book book, ShoppingCart cart) {
        CartItem cartItem = itemMapper.toCartItem(itemDto);
        cartItem.setBook(book);
        cart.addItemToCart(cartItem);
    }
}
