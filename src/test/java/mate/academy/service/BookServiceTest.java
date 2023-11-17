package mate.academy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.mapper.BookMapper;
import mate.academy.mapper.BookMapperImpl;
import mate.academy.model.Book;
import mate.academy.model.Category;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CategoryRepository;
import mate.academy.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper mapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    @DisplayName("""
            Verify correct BookDto was returned when the book was saved""")
    void save_WithValidDto_ShouldReturnCorrectBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Rich Dad Poor Dad",
                "R.Kiyosaki",
                "1234567890",
                BigDecimal.valueOf(14.99),
                null, null,
                List.of(1L, 2L)
        );

        Book book = new Book();
        book.setTitle(requestDto.title());
        book.setAuthor(requestDto.author());
        book.setIsbn(requestDto.isbn());
        book.setPrice(requestDto.price());
        book.setCategories(defaultCategories().collect(Collectors.toSet()));

        defaultCategories().forEach(c -> when(categoryRepository.getReferenceById(anyLong())).thenReturn(c));
        when(bookRepository.save(book)).thenReturn(book);

        BookDto bookDto = bookService.save(requestDto);

        assertThat(bookDto)
                .hasFieldOrPropertyWithValue("title", "Rich Dad Poor Dad")
                .hasFieldOrPropertyWithValue("author", "R.Kiyosaki")
                .hasFieldOrPropertyWithValue("isbn", "1234567890")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(14.99));
    }

    private Book getDefaultBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("author");
        book.setIsbn("1234567890");
        book.setPrice(BigDecimal.TEN);
        book.setDescription("description");
        book.setCoverImage("cover_image");
        book.setCategories(Set.of(getDefaultCategory()));
        return book;
    }

    private Stream<Category> defaultCategories() {
        Category first = new Category();
        Category second = new Category();
        first.setId(1L);
        second.setId(2L);
        return Stream.of(first, second);
    }
}
