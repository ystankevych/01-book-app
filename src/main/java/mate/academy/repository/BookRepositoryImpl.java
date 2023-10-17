package mate.academy.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.exception.DataProcessingException;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.Book;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Override
    public Book save(Book book) {
        try {
            sessionFactory.inTransaction(s -> s.persist(book));
            return book;
        } catch (Exception e) {
            throw new DataProcessingException("Can't save book: " + book, e);
        }
    }

    @Override
    public List<Book> findAll() {
        try {
            return sessionFactory.fromSession(s -> s.createQuery("FROM Book", Book.class)
                    .getResultList());
        } catch (Exception e) {
            throw new DataProcessingException("Fetching books failed", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try {
            return sessionFactory.fromSession(s -> Optional.ofNullable(s.get(Book.class, id)));
        } catch (Exception e) {
            throw new EntityNotFoundException("Getting book by id " + id + " failed", e);
        }
    }
}
