package mate.academy.mapper;

import mate.academy.dto.BookDto;
import mate.academy.dto.CreateBookRequestDto;
import mate.academy.model.Book;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        implementationPackage = "<PACKAGE_NAME>.impl")
public interface BookMapper {
    @Mapping(target = "description", defaultValue = "undefined")
    @Mapping(target = "coverImage", defaultValue = "undefined")
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);
}
