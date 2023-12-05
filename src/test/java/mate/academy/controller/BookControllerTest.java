package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.BookApiErrorResponse;
import mate.academy.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/create/add-default-categories.sql",
        "classpath:database/create/add-default-books.sql",
        "classpath:database/create/add-into-books-categories-table.sql"})
@Sql(scripts = {"classpath:database/delete/delete-books-categories-table.sql",
        "classpath:database/delete/delete-all-books.sql",
        "classpath:database/delete/delete-all-categories.sql"},
        executionPhase = AFTER_TEST_METHOD)
class BookControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Create a new book with valid request dto Ok")
    void createBook_ValidRequestDto_Ok() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto("book", "author",
                "3333333333", BigDecimal.valueOf(20.99), null, null, List.of(1L));

        MvcResult result = mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );
        assertAll("actual",
                () -> assertNotNull(actual.id()),
                () -> assertEquals(actual.title(), requestDto.title()),
                () -> assertEquals(actual.author(), requestDto.author()),
                () -> assertEquals(actual.isbn(), requestDto.isbn()),
                () -> assertEquals(actual.price(), requestDto.price()),
                () -> assertIterableEquals(actual.categoriesId(), requestDto.categoriesId()));
    }

    @ParameterizedTest
    @DisplayName("Create book with invalid request dto not Ok")
    @MethodSource("invalidRequestDto")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_InvalidRequestDto_NotOk(CreateBookRequestDto request,
                                            String fieldName,
                                            String errorMessage) throws Exception {
        MvcResult result = mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        BookApiErrorResponse errorResponse = objectMapper.readValue(result.getResponse()
                        .getContentAsString(), BookApiErrorResponse.class);
        Map<String, String> erroreMap = (Map<String, String>) errorResponse.errorMessages();
        assertTrue(erroreMap.get(fieldName).contains(errorMessage));
    }

    private static Stream<Arguments> invalidRequestDto() {
        return Stream.of(
                Arguments.of(new CreateBookRequestDto(null, "author", "3333333333",
                        BigDecimal.valueOf(14.99), null, null, List.of(1L)),
                        "title", "Title must not be null or empty"),
                Arguments.of(new CreateBookRequestDto("", "author", "3333333333",
                        BigDecimal.valueOf(14.99), null, null, List.of(1L)),
                        "title", "Title must not be null or empty"),
                Arguments.of(new CreateBookRequestDto("title", "", "3333333333",
                        BigDecimal.valueOf(14.99), null, null, List.of(1L)),
                        "author", "Author must not be null or empty"),
                Arguments.of(new CreateBookRequestDto("title", null, "3333333333",
                        BigDecimal.valueOf(14.99), null, null, List.of(1L)),
                        "author", "Author must not be null or empty"),
                Arguments.of(new CreateBookRequestDto("title", "author", null,
                        BigDecimal.valueOf(14.99), null, null, List.of(1L)),
                        "isbn", "Isbn must not be null or empty"),
                Arguments.of(new CreateBookRequestDto("title", "author", "",
                        BigDecimal.valueOf(14.99), null, null, List.of(1L)),
                        "isbn", "Isbn must not be null or empty"),
                Arguments.of(new CreateBookRequestDto("title", "author", "33333",
                        BigDecimal.valueOf(14.99), null, null, List.of(1L)),
                        "isbn", "Required length 10-13 digits"),
                Arguments.of(new CreateBookRequestDto("title", "author", "3333333333",
                        null, null, null, List.of(1L)),
                        "price", "Price must not be null"),
                Arguments.of(new CreateBookRequestDto("title", "author", "3333333333",
                        BigDecimal.valueOf(-1), null, null, List.of(1L)),
                        "price", "Price must not be negative")
        );
    }

    @Test
    @DisplayName("Update existing book with new title and author is Ok")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_ExistingBookWithValidRequestDto_Ok() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto("new", "new", "1111111111",
                BigDecimal.valueOf(10.99), null, null, List.of(1L, 2L));

        MvcResult result = mockMvc.perform(put("/books/1")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertAll("actual",
                () -> assertEquals(1L, actual.id()),
                () -> assertEquals(actual.title(), requestDto.title()),
                () -> assertEquals(actual.author(), requestDto.author()),
                () -> assertEquals(actual.isbn(), requestDto.isbn()),
                () -> assertEquals(actual.price(), requestDto.price()),
                () -> assertIterableEquals(actual.categoriesId(), requestDto.categoriesId()));
    }

    @Test
    @DisplayName("Update non-existent book not ok")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_NonExistentBook_ThrowAnException() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto("new", "new", "1111111111",
                BigDecimal.valueOf(10.99), null, null, List.of(1L, 2L));

        mockMvc.perform(put("/books/10")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("Book with id: %s not found"
                        .formatted(10), result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("Getting all books is Ok")
    @WithMockUser(username = "user", roles = "USER")
    void getAll_ListOfBooks_Ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertAll("actual",
                () -> assertIterableEquals(List.of(1L, 2L), actual.stream()
                        .map(BookDto::id).toList()),
                () -> assertIterableEquals(List.of("Kiyosaki", "Schaefer"), actual.stream()
                        .map(BookDto::author).toList()),
                () -> assertIterableEquals(List.of("Rich Dad Poor Dad", "A Dog Named Money"),
                        actual.stream().map(BookDto::title).toList()),
                () -> assertIterableEquals(List.of("1111111111", "2222222222"),
                        actual.stream().map(BookDto::isbn).toList()),
                () -> assertIterableEquals(List.of(BigDecimal.valueOf(10.99),
                                BigDecimal.valueOf(11.99)),
                        actual.stream().map(BookDto::price).toList()),
                () -> assertIterableEquals(List.of(List.of(1L, 2L), List.of(1L, 2L)),
                        actual.stream().map(BookDto::categoriesId).toList()));
    }

    @Test
    @DisplayName("Getting book by correct id is Ok")
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_ValidId_Ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        assertAll("actual",
                () -> assertEquals(1L, actual.id()),
                () -> assertEquals("Rich Dad Poor Dad", actual.title()),
                () -> assertEquals("Kiyosaki", actual.author()),
                () -> assertEquals("1111111111", actual.isbn()),
                () -> assertEquals(BigDecimal.valueOf(10.99), actual.price()),
                () -> assertIterableEquals(List.of(1L, 2L), actual.categoriesId()));
    }

    @Test
    @DisplayName("""
            Getting a book with an invalid id will throw an exception""")
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_InvalidId_ShouldThrowException() throws Exception {
        mockMvc.perform(get("/books/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("Book with id: %s not found"
                        .formatted(10), result.getResolvedException().getMessage()));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L})
    @DisplayName("Deleting book by existing and non-existent id Ok")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_ByExistingAndNonExistentId_Ok(Long id) throws Exception {
        mockMvc.perform(delete("/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
