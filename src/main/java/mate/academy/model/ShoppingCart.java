package mate.academy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    private Long id;
    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private User user;
    @OneToMany(mappedBy = "shoppingCart")
    private Set<CartItem> cartItems;
}
