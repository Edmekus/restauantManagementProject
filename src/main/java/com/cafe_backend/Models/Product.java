package com.cafe_backend.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;


@NamedQuery(name = "Product.getAllProducts", query = "select new com.cafe_backend.wrapper.ProductWrapper" +
        "(p.id,p.name,p.description,p.price,p.status,p.category.id,p.category.name) from Product p " )

@NamedQuery(name = "Product.updateProductStatus", query = "update Product p set p.status=:status  where p.id=:id")

@NamedQuery(name = "Product.getProductByCategory", query = "select new com.cafe_backend.wrapper.ProductWrapper " +
        "(p.id,p.name) from Product p where p.category.id=:id and p.status= 'true' ")

@NamedQuery(name = "Product.getProductById", query = "select new com.cafe_backend.wrapper.ProductWrapper" +
        "(p.id,p.name,p.description,p.price) from Product p where p.id=:id ")

@Entity
@DynamicInsert
@DynamicUpdate
@Data
@Table(name = "product")
public class Product implements Serializable {

    // Déclaration d'un identifiant unique pour la sérialisation
    @Serial
    private static final long serialVersionUID = 123L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_product", nullable = false)
    private Category category;

    private String description;

    private Integer price;

    private String status;

}
