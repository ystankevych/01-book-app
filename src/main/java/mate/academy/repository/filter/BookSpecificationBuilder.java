package mate.academy.repository.filter;

import mate.academy.dto.book.BookSearchParametersDto;
import mate.academy.model.Book;
import org.springframework.data.jpa.domain.Specification;

public interface BookSpecificationBuilder {
    Specification<Book> buildSpecification(BookSearchParametersDto bookDto);
}
