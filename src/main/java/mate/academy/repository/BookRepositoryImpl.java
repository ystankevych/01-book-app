package mate.academy.repository;

import java.util.List;
import mate.academy.exception.DataProcessingException;
import mate.academy.model.Book;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public BookRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

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
}
