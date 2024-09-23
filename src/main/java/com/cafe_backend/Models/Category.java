package com.cafe_backend.Models;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;

@NamedQuery(name = "Category.getAllCategory" , query = "SELECT c FROM Category c WHERE " +
        "c.id IN (SELECT p.category.id FROM Product p WHERE p.status='true' )")

@Entity
@DynamicInsert
@DynamicUpdate
@Data
@Table(name = "category")
public class Category implements Serializable {

    // Déclaration d'un identifiant unique pour la sérialisation
    @Serial
    private static final long serialVersionUID = 12L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String name;


}
