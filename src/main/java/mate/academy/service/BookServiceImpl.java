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
import mate.academy.repository.BookSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationProvider provider;

    @Override
    public BookDto save(CreateBookRequestDto book) {
        Book savedBook = bookRepository.save(bookMapper.toBook(book));
        return bookMapper.toDto(savedBook);
    }

    @Override
    public BookDto updateBook(Long id, CreateBookRequestDto book) {
        Book bookFromDb = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't update book with id " + id));
        bookMapper.updateBookFromDto(book, bookFromDb);
        return bookMapper.toDto(bookRepository.save(bookFromDb));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id " + id)));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> findByParameters(BookSearchParametersDto bookSearchDto) {
        Specification<Book> filter = Specification.where(
                        CollectionUtils.isEmpty(bookSearchDto.getTitles()) ? null :
                                provider.titleContainsIgnoreCase(bookSearchDto.getTitles()))
                .and(CollectionUtils.isEmpty(bookSearchDto.getAuthors()) ? null :
                        provider.authorIn(bookSearchDto.getAuthors()));
        return bookRepository.findAll(filter)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
