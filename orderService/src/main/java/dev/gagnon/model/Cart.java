
package dev.gagnon.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="items_cart")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Cart {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String cartId;
    private String buyerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToMany(cascade={CascadeType.ALL}, orphanRemoval=true)
    @JsonIgnore
    private List<CartItem> items = new ArrayList();

}

