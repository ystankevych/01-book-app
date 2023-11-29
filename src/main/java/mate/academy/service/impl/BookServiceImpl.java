package mate.academy.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.BookSearchParametersDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.model.Category;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CategoryRepository;
import mate.academy.repository.filter.BookSpecificationBuilder;
import mate.academy.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder builder;

    @Transactional
    @Override
    public BookDto updateBook(Long id, CreateBookRequestDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getNotFoundMessage(id)));
        bookMapper.updateBookFromDto(bookDto, book);
        book.setCategories(categoriesIdToCategories(bookDto.categoriesId()));
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookDto save(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toBook(bookDto);
        book.setCategories(categoriesIdToCategories(bookDto.categoriesId()));
        return bookMapper.toDto(bookRepository.save(book));
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
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> findByParameters(BookSearchParametersDto bookSearchDto,
                                                            Pageable pageable) {
        return bookRepository.findAll(builder.buildSpecification(bookSearchDto), pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId) {
        return bookRepository.findByCategories_Id(categoryId).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    private String getNotFoundMessage(Long id) {
        return String.format("Book with id: %s not found", id);
    }

    private Set<Category> categoriesIdToCategories(List<Long> categories) {
        return categories.stream()
                .map(categoryRepository::getReferenceById)
                .collect(Collectors.toSet());
    }
}
