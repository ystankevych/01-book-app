package mate.academy.service;

import java.util.List;
import mate.academy.dto.BookDto;
import mate.academy.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    List<BookDto> findAll();

    BookDto findById(Long id);
}
