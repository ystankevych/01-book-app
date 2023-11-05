package mate.academy.mapper;

import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.model.Book;
import mate.academy.model.Category;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BookMapper {
    @Mapping(source = "categories", target = "categoriesId")
    BookDto toDto(Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    Book toBook(CreateBookRequestDto createBookRequestDto);

    void updateBookFromDto(CreateBookRequestDto book, @MappingTarget Book entity);

    default Long categoryToId(Category category) {
        return category.getId();
    }
}
