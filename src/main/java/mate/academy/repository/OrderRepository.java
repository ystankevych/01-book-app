package mate.academy.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.book"})
    List<Order> findAllByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.book"})
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}
