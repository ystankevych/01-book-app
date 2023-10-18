package mate.academy.service;

import java.util.List;
import mate.academy.dto.BookDto;
import mate.academy.dto.BookSearchParametersDto;
import mate.academy.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    BookDto updateBook(Long id, CreateBookRequestDto book);

    List<BookDto> findAll();

    BookDto findById(Long id);

    void deleteById(Long id);

    List<BookDto> findByParameters(BookSearchParametersDto bookSearchParametersDto);
}
