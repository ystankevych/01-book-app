package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

import java.util.List;
import mate.academy.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/create/add-default-categories.sql",
        "classpath:database/create/add-default-books.sql",
        "classpath:database/create/add-into-books-categories-table.sql"})
@Sql(scripts = {"classpath:database/delete/delete-books-categories-table.sql",
        "classpath:database/delete/delete-all-books.sql",
        "classpath:database/delete/delete-all-categories.sql"},
        executionPhase = AFTER_TEST_METHOD)
class BookRepositoryTest {
    @Autowired
    private BookRepository repository;

    @Test
    @DisplayName("Getting list of books by existing category id Ok")
    void findByCategories_Id_ExistingId_Ok() {
        List<Book> actual = repository.findByCategories_Id(1L);

        assertAll("actual",
                () -> assertEquals(2, actual.size()),
                () -> assertIterableEquals(List.of(1L, 2L),
                        actual.stream().map(Book::getId).toList()),
                () -> assertIterableEquals(List.of("Rich Dad Poor Dad", "A Dog Named Money"),
                        actual.stream().map(Book::getTitle).toList()));
    }
}
