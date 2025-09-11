package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="wislist")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WishList {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String buyerEmail;
}

