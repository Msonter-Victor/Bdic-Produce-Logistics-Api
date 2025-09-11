/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.model.User
 *  dev.gagnon.model.User$UserBuilder
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.Table
 *  lombok.Generated
 */
package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String email;
    @Column(nullable=false)
    private String firstName;
    private String lastName;
    private String phone;
    private boolean isDisabled;

}

