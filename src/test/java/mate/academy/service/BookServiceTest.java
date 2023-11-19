package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.model.Category;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CategoryRepository;
import mate.academy.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private BookMapper mapper = Mappers.getMapper(BookMapper.class);

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("""
            Verify correct BookDto was returned when the book was saved""")
    void save_WithValidDto_ShouldReturnCorrectBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Rich Dad Poor Dad", "Kiyosaki", "111",
                BigDecimal.ONE, null, null, List.of(1L, 2L));

        Category category = categoryById(1L);

        Book unsavedBook = defaultBook(requestDto.title(), requestDto.author(), requestDto.isbn(),
                requestDto.price(), Set.of(category));

        Book savedBook = defaultBook(unsavedBook.getTitle(), unsavedBook.getAuthor(),
                unsavedBook.getIsbn(), unsavedBook.getPrice(), unsavedBook.getCategories());
        savedBook.setId(1L);

        when(categoryRepository.getReferenceById(anyLong())).thenReturn(category);
        when(bookRepository.save(unsavedBook)).thenReturn(savedBook);

        BookDto expected = dtoFromBook(savedBook);
        BookDto actual = bookService.save(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update existing book with new isbn and categories")
    void updateBook_ExistingBook_ShouldReturnCorrectBookDto() {
        Book bookFromDb = defaultBook("Rich Dad Poor Dad", "Kiyosaki",
                "111", BigDecimal.ONE, Set.of(categoryById(1L), categoryById(2L)));
        bookFromDb.setId(1L);

        CreateBookRequestDto requestDto = new CreateBookRequestDto("Rich Dad Poor Dad", "Kiyosaki",
                "222", BigDecimal.ONE, null, null, List.of(1L));

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookFromDb));
        bookFromDb.setIsbn(requestDto.isbn());
        bookFromDb.setCategories(Set.of(categoryById(1L)));

        when(categoryRepository.getReferenceById(anyLong())).thenReturn(categoryById(1L));
        when(bookRepository.save(bookFromDb)).thenReturn(bookFromDb);

        BookDto expected = dtoFromBook(bookFromDb);
        BookDto actual = bookService.updateBook(1L, requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Checking updating a book with an invalid id will throw an exception""")
    void updateBook_WithInvalidId_ShouldThrowException() {
        Long invalidId = 100L;
        CreateBookRequestDto requestDto = defaultRequestDto("title", "author", "111",
                BigDecimal.ONE, List.of(1L));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(invalidId, requestDto));
    }

    @Test
    @DisplayName("""
            Verify correct list of BookDto was returned after""")
    void findAll_Book_ShouldReturnListOfDto() {
        Book book = defaultBook("Rich Dad Poor Dad", "Kiyosaki", "111",
                BigDecimal.ONE, Set.of(categoryById(1L)));
        book.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = Collections.singletonList(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        List<BookDto> actual = bookService.findAll(pageable);

        assertAll("actual",
                () -> assertEquals(1, actual.size()),
                () -> assertTrue(actual.contains(dtoFromBook(book))));
    }

    @Test
    @DisplayName("Getting a book by an existing id returns correct BookDto")
    void findById_WithValidId_ShouldReturnCorrectDto() {
        Long bookId = 1L;
        Book bookFromDb = defaultBook("Rich Dad Poor Dad", "Kiyosaki", "111",
                BigDecimal.ONE, Set.of(categoryById(1L)));
        bookFromDb.setId(bookId);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookFromDb));

        BookDto expected = dtoFromBook(bookFromDb);
        BookDto actual = bookService.findById(bookId);

        assertEquals(expected, actual, "expected result is: " + expected);
    }

    @Test
    @DisplayName("Retrieving a book by an invalid ID resulted in an exception")
    void findById_WithInvalidId_ShouldThrowException() {
        Long invalidId = -1L;

        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(invalidId));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, -1L})
    void deleteById_WithPositiveAndNegativeId_Ok(Long id) {

        bookService.deleteById(id);

        verify(bookRepository).deleteById(id);
    }

    private CreateBookRequestDto defaultRequestDto(String title, String author,
                                                   String isbn, BigDecimal price, List<Long> ids) {
        return new CreateBookRequestDto(title, author, isbn, price, null, null, ids);
    }

    private Book defaultBook(String title, String author, String isbn,
                             BigDecimal price, Set<Category> categories) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setCategories(categories);
        return book;
    }

    private BookDto dtoFromBook(Book book) {
        return new BookDto(
                book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getPrice(), book.getDescription(), book.getCoverImage(),
                categoriesIdFromCategories(book.getCategories())
        );
    }

    private List<Long> categoriesIdFromCategories(Set<Category> categories) {
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());
    }

    private Category categoryById(Long id) {
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
