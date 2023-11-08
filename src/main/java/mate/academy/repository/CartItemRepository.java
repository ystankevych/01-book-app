package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCartId(Long itemId, Long cartId);

    Optional<CartItem> findByShoppingCartIdAndBookId(Long cartId, Long bookId);

    void deleteAllByBookId(Long bookId);

    void deleteAllByShoppingCart_Id(Long cartId);
}
