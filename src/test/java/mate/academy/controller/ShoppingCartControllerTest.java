package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.dto.cartitem.CartItemDto;
import mate.academy.dto.cartitem.CartItemRequestDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.exception.BookApiErrorResponse;
import mate.academy.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/create/add-default-user-cart.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class ShoppingCartControllerTest {
    private static final String ADD_BOOK = "classpath:database/create/add-one-book.sql";
    private static final String CLEAR_CART = "classpath:database/delete/delete-cart-items.sql";
    private static final String DELETE_BOOK = "classpath:database/delete/delete-all-books.sql";
    private static final String DELETE_USER_CART = "classpath:database/delete/delete-cart-user.sql";
    private static final String ADD_CART_ITEM = "classpath:database/create/add-cart-item.sql";
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails(value = "user@ukr.net")
    @Sql(scripts = DELETE_USER_CART, executionPhase = AFTER_TEST_METHOD)
    void getShoppingCart_EmptyCart_ReturnCorrectCartDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertEquals(1L, actual.id());
    }

    @Test
    @WithUserDetails(value = "user@ukr.net")
    @Sql(scripts = ADD_BOOK)
    @Sql(scripts = {CLEAR_CART, DELETE_BOOK, DELETE_USER_CART},
            executionPhase = AFTER_TEST_METHOD)
    void addBookToCart_ExistingBookValidRequestDto_Ok() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 2);

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartItemDto expected = new CartItemDto(1L, 1L, "Rich Dad Poor Dad", 2);
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertAll("actual",
                () -> assertEquals(1, actual.id()),
                () -> assertEquals(1, actual.userId()),
                () -> assertEquals(1, actual.cartItems().size()),
                () -> assertTrue(actual.cartItems().contains(expected)));
    }

    @Test
    @WithUserDetails(value = "user@ukr.net")
    @Sql(scripts = ADD_BOOK)
    @Sql(scripts = {CLEAR_CART, DELETE_BOOK, DELETE_USER_CART},
            executionPhase = AFTER_TEST_METHOD)
    void addBookToCart_NonExistentBook_ShouldThrowException() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(10L, 2);

        mockMvc.perform(post("/cart")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("Book with id: %d is not found"
                        .formatted(requestDto.bookId()), result.getResolvedException().getMessage()));
    }

    @ParameterizedTest
    @Sql(scripts = DELETE_USER_CART, executionPhase = AFTER_TEST_METHOD)
    @MethodSource("invalidCartItemRequestDto")
    @WithUserDetails(value = "user@ukr.net")
    void addBookToCart_InvalidRequestDto_NotOk(CartItemRequestDto requestDto,
                                               String fieldName,
                                               String errorMessage) throws Exception {
        MvcResult result = mockMvc.perform(post("/cart")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        BookApiErrorResponse errorResponse = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookApiErrorResponse.class);
        Map<String, String> erroreMap = (Map<String, String>) errorResponse.errorMessages();
        assertEquals(erroreMap.get(fieldName), errorMessage);
    }

    private static Stream<Arguments> invalidCartItemRequestDto() {
        return Stream.of(
                Arguments.of(new CartItemRequestDto(-1L, 2),
                        "bookId", "Book id must be positive"),
                Arguments.of(new CartItemRequestDto(null, 2),
                        "bookId", "Book id must not be null"),
                Arguments.of(new CartItemRequestDto(1L, 0),
                        "quantity", "Quantity must be positive"),
                Arguments.of(new CartItemRequestDto(1L, -1),
                        "quantity", "Quantity must be positive")
        );
    }

    @Test
    @WithUserDetails(value = "user@ukr.net")
    @Sql(scripts = {ADD_BOOK, ADD_CART_ITEM})
    @Sql(scripts = {CLEAR_CART, DELETE_BOOK, DELETE_USER_CART},
            executionPhase = AFTER_TEST_METHOD)
    void updateBookQuantity_ExistingCartItem_Ok() {
    }
}
