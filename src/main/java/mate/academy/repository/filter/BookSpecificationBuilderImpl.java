package mate.academy.repository.filter;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.BookSearchParametersDto;
import mate.academy.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilderImpl implements BookSpecificationBuilder {
    private final BookSpecificationProvider provider;

    @Override
    public Specification<Book> buildSpecification(BookSearchParametersDto bookSearchDto) {
        return Specification.where(
                        CollectionUtils.isEmpty(bookSearchDto.titles()) ? null :
                                provider.titleContainsIgnoreCase(bookSearchDto.titles()))
                .and(CollectionUtils.isEmpty(bookSearchDto.authors()) ? null :
                        provider.authorIn(bookSearchDto.authors()));
    }
}
