package mate.academy.repository.filter;

import jakarta.persistence.criteria.Predicate;
import java.util.List;
import mate.academy.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationProvider {
    private static final String AUTHOR_COLUMN_NAME = "author";
    private static final String TITLE_COLUMN_NAME = "title";

    public Specification<Book> titleContainsIgnoreCase(List<String> titles) {
        return (root, query, builder) -> builder.or(titles.stream()
                .map(t -> builder.like(builder.lower(root.get(TITLE_COLUMN_NAME)),
                        "%" + t.toLowerCase() + "%"))
                .toArray(Predicate[]::new));
    }

    public Specification<Book> authorIn(List<String> authors) {
        return ((root, query, builder) -> root.get(AUTHOR_COLUMN_NAME).in(authors.toArray()));
    }
}
