package dev.gagnon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@Entity
@Table(name = "wislist")
public class WishList {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String buyerEmail;

}
