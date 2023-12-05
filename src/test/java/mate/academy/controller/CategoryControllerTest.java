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
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
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
@Sql(scripts = "classpath:database/delete/delete-all-categories.sql",
        executionPhase = AFTER_TEST_METHOD)
@Sql(scripts = "classpath:database/create/add-default-categories.sql")
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    private static final String ADD_DEFAULT_CATEGORIES = """
            classpath:database/create/add-default-categories.sql""";
    private static final String ADD_DEFAULT_BOOKS = """
            classpath:database/create/add-default-books.sql""";
    private static final String ADD_BOOKS_CATEGORIES = """
            classpath:database/create/add-into-books-categories-table.sql""";
    private static final String DELETE_ALL_BOOKS_CATEGORIES = """
            classpath:database/delete/delete-books-categories-table.sql""";
    private static final String DELETE_ALL_CATEGORIES = """
                        
            classpath:database/delete/delete-all-categories.sql""";
    private static final String DELETE_ALL_BOOKS = """
            classpath:database/delete/delete-all-books.sql""";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .apply(springSecurity())
                .build();
    }

    @ParameterizedTest
    @MethodSource("validCategoryRequestDto")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_WithValidRequestDto_Ok(CategoryRequestDto requestDto,
                                               Long id) throws Exception {
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        System.out.println(actual);
        assertAll("actual",
                () -> assertNotNull(actual.id()),
                () -> assertEquals(requestDto.name(), actual.name()),
                () -> assertEquals(requestDto.description(), actual.description()));
    }

    private static Stream<Arguments> validCategoryRequestDto() {
        return Stream.of(
                Arguments.of(new CategoryRequestDto("Investment", "Investment books"), 1L),
                Arguments.of(new CategoryRequestDto("Investment", ""), 2L),
                Arguments.of(new CategoryRequestDto("Investment", null), 3L)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequestDto")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_WithInvalidRequestDto_NotOk(CategoryRequestDto requestDto,
                                                    String errorMessage,
                                                    String fieldName) throws Exception {
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        BookApiErrorResponse errorResponse = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookApiErrorResponse.class);
        Map<String, String> erroreMap = (Map<String, String>) errorResponse.errorMessages();
        assertEquals(erroreMap.get(fieldName), errorMessage);
    }

    private static Stream<Arguments> invalidRequestDto() {
        return Stream.of(
                Arguments.of(new CategoryRequestDto(null, "description"),
                        "Category name must not be null or empty", "name"),
                Arguments.of(new CategoryRequestDto("", "description"),
                        "Category name must not be null or empty", "name"),
                Arguments.of(new CategoryRequestDto("name", "description".repeat(24)),
                        "Maximum allowed size 255 characters", "description")
        );
    }

    @Test
    @DisplayName("Update existing category with new description is Ok")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_WithValidIdAndRequestDto_Ok() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("investment",
                "books on investments and finance");
        MvcResult result = mockMvc.perform(put("/categories/1")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertAll("actual",
                () -> assertEquals(1L, actual.id()),
                () -> assertEquals(requestDto.name(), actual.name()),
                () -> assertEquals(requestDto.description(), actual.description()));
    }

    @Test
    @DisplayName("""
            Updating a category with an invalid id will throw an exception""")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_ByNonExistentId_ThrowAnException() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("investment", "new description");
        mockMvc.perform(put("/categories/10")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("No such a category with id: %d"
                        .formatted(10), result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("Getting all categories is Ok")
    @WithMockUser(username = "user", roles = "USER")
    void getAll_ListOfCategories_Ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<CategoryDto> expected = List.of(
                new CategoryDto(1L, "investment", "investment books"),
                new CategoryDto(2L, "children", "books for children"));

        List<CategoryDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertIterableEquals(expected, actual);
    }

    @Test
    @DisplayName("Getting category by correct id is Ok")
    @WithMockUser(username = "user", roles = "USER")
    void getCategoryById_ValidId_Ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertAll("actual",
                () -> assertEquals(1L, actual.id()),
                () -> assertEquals(actual.name(), "investment"),
                () -> assertEquals(actual.description(), "investment books"));
    }

    @Test
    @DisplayName("""
            Getting a category with an invalid id will throw an exception""")
    @WithMockUser(username = "user", roles = "USER")
    void getCategoryById_InvalidId_ShouldThrowException() throws Exception {
        mockMvc.perform(get("/categories/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("No such a category with id: %d"
                        .formatted(10), result.getResolvedException().getMessage()));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L})
    @DisplayName("Deleting by existing and non-existent id Ok")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCategory_ByExistingAndNonExistentId_Ok(Long id) throws Exception {
        mockMvc.perform(delete("/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Getting list of books by existing id is Ok")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {ADD_DEFAULT_CATEGORIES, ADD_DEFAULT_BOOKS, ADD_BOOKS_CATEGORIES})
    @Sql(scripts = {DELETE_ALL_BOOKS_CATEGORIES, DELETE_ALL_BOOKS, DELETE_ALL_CATEGORIES},
            executionPhase = AFTER_TEST_METHOD)
    void getBooksByCategoryId_ByExistingId_Ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/1/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<>() {});

        assertAll("actual",
                () -> assertIterableEquals(List.of(1L, 2L),
                        actual.stream().map(BookDtoWithoutCategoryIds::id).toList()),
                () -> assertIterableEquals(List.of("Rich Dad Poor Dad", "A Dog Named Money"),
                        actual.stream().map(BookDtoWithoutCategoryIds::title).toList()),
                () -> assertIterableEquals(List.of("Kiyosaki", "Schaefer"),
                        actual.stream().map(BookDtoWithoutCategoryIds::author).toList()),
                () -> assertIterableEquals(List.of("1111111111", "2222222222"),
                        actual.stream().map(BookDtoWithoutCategoryIds::isbn).toList()),
                () -> assertIterableEquals(List.of(BigDecimal.valueOf(10.99),
                                BigDecimal.valueOf(11.99)), actual.stream()
                        .map(BookDtoWithoutCategoryIds::price).toList()));
    }

    @Test
    @DisplayName("""
            Getting books by non-existent id return empty list""")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = {ADD_DEFAULT_CATEGORIES, ADD_DEFAULT_BOOKS, ADD_BOOKS_CATEGORIES})
    @Sql(scripts = {DELETE_ALL_BOOKS_CATEGORIES, DELETE_ALL_BOOKS, DELETE_ALL_CATEGORIES},
            executionPhase = AFTER_TEST_METHOD)
    void getBooksByCategoryId_ByNonExistentId_ShouldReturnEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/10/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<>() {});

        assertTrue(actual.isEmpty());
    }
}
