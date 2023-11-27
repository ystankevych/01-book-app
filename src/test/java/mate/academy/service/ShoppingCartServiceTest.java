package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.cartitem.UpdateCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.mapper.ShoppingCartMapperImpl;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private BookRepository bookRepository;

    @Spy
    private CartItemMapper cartItemMapper = Mappers.getMapper(CartItemMapper.class);

    @Spy
    private ShoppingCartMapper shoppingCartMapper = new ShoppingCartMapperImpl(cartItemMapper);

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Getting shopping cart by user id Ok")
    void getUsersCart_ByUserId_ShouldReturnCorrectDto() {
        ShoppingCart shoppingCart = defaultShoppingCart(1L, defaultUser(1L));

        when(shoppingCartRepository.findByUserId(anyLong())).thenReturn(shoppingCart);

        ShoppingCartDto actual = shoppingCartService.getUsersCart(1L);

        assertAll("actual",
                () -> assertEquals(1L, actual.id()),
                () -> assertEquals(1L, actual.userId()));
    }

    @Test
    void addBookToCart_ValidRequestDto_ShouldReturnCorrectCartDto() {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 1);
        User user = defaultUser(1L);
        Book book = defaultBook(1L, "Rich Dad Poor Dad");
        ShoppingCart shoppingCart = defaultShoppingCart(1L, user);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);

        ShoppingCartDto actual = shoppingCartService.addBookToCart(user.getId(), requestDto);
        assertAll("actual",
                () -> assertEquals(actual.id(), 1L),
                () -> assertEquals(actual.userId(), 1L),
                () -> assertEquals(1, actual.cartItems().size()),
                () -> assertEquals(1, actual.cartItems().stream()
                        .filter(i -> i.bookId().equals(1L) && i.bookTitle().equals(book.getTitle())
                        && i.quantity() == 1).count()));
    }

    @Test
    void addBookToCart_RequestDtoWithNonExistentBook_NotOk() {
        CartItemRequestDto requestDto = new CartItemRequestDto(10L, 1);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToCart(1L, requestDto),
                "EntityNotFoundException was expected");
    }

    @Test
    void updateBookQuantity_ValidCartItemId_Ok() {
        ShoppingCart shoppingCart = defaultShoppingCart(1L, defaultUser(1L));
        CartItem cartItem = defaultCartItem(defaultBook(1L, "Rich Dad Poor Dad"),
                1);
        shoppingCart.addItemToCart(cartItem);

        when(shoppingCartRepository.findByUserId(anyLong())).thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        UpdateCartItemDto requestDto = new UpdateCartItemDto(5);
        ShoppingCartDto actual = shoppingCartService.updateBookQuantity(1L, 1L, requestDto);

        assertAll("actual",
                () -> assertEquals(1, actual.cartItems().size()),
                () -> assertEquals(1, actual.cartItems().stream()
                        .filter(i -> i.id().equals(1L) && i.bookId().equals(1L)
                        && i.quantity().equals(5)).count()));
    }

    @Test
    void updateBookQuantity_InvalidCartItemId_NotOk() {
        UpdateCartItemDto requestDto = new UpdateCartItemDto(5);
        ShoppingCart shoppingCart = defaultShoppingCart(1L, defaultUser(1L));

        when(shoppingCartRepository.findByUserId(anyLong()))
                .thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateBookQuantity(1L, 1L, requestDto));
    }

    @Test
    void deleteBookFromCart_ExistingCartItemId_Ok() {
        ShoppingCart shoppingCart = defaultShoppingCart(1L, defaultUser(1L));
        CartItem cartItem = defaultCartItem(defaultBook(1L, "title"), 1);
        shoppingCart.addItemToCart(cartItem);

        when(shoppingCartRepository.findByUserId(anyLong())).thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(1L, 1L))
                .thenReturn(Optional.of(cartItem));

        shoppingCartService.deleteBookFromCart(1L, 1L);

        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void deleteBookFromCart_NonExistentCartItem_ThrowException() {
        ShoppingCart shoppingCart = defaultShoppingCart(1L, defaultUser(1L));

        when(shoppingCartRepository.findByUserId(anyLong())).thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.deleteBookFromCart(1L, 5L));
    }

    private User defaultUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private ShoppingCart defaultShoppingCart(Long id, User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(id);
        shoppingCart.setUser(user);
        return shoppingCart;
    }

    private Book defaultBook(Long id, String title) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        return book;
    }

    private CartItem defaultCartItem(Book book, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);
        return cartItem;
    }
}
