package mate.academy.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE shopping_carts SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted=false")
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "shoppingCart", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private Set<CartItem> cartItems = new HashSet<>();

    private boolean isDeleted;
}
