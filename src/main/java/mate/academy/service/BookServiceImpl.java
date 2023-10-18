package mate.academy.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.BookDto;
import mate.academy.dto.BookSearchParametersDto;
import mate.academy.dto.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.BookRepository;
import mate.academy.repository.filter.BookSpecificationBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder builder;

    @Override
    public BookDto save(CreateBookRequestDto book) {
        Book savedBook = bookRepository.save(bookMapper.toBook(book));
        return bookMapper.toDto(savedBook);
    }

    @Override
    public BookDto updateBook(Long id, CreateBookRequestDto book) {
        Book bookFromDb = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getNotFoundMessage(id)));
        bookMapper.updateBookFromDto(book, bookFromDb);
        return bookMapper.toDto(bookRepository.save(bookFromDb));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(getNotFoundMessage(id)));
    }

    @Override
    public void deleteById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(getNotFoundMessage(id));
        }
    }

    @Override
    public List<BookDto> findByParameters(BookSearchParametersDto bookSearchDto,
                                          Pageable pageable) {
        return bookRepository.findAll(builder.buildSpecification(bookSearchDto), pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    private String getNotFoundMessage(Long id) {
        return String.format("Book with id: %s not found", id);
    }
}
