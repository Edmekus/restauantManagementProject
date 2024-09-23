package com.cafe_backend.Models;

// Importations nécessaires pour la persistance et les annotations
import jakarta.persistence.*;  // Importation des annotations pour la persistance
import lombok.Data;  // Importation de Lombok pour générer les méthodes getters, setters, equals, et toString
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;  // Importation pour la gestion de la sérialisation
import java.time.LocalDateTime;


@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
@NamedQuery(name = "User.getAllUser",query = "SELECT com.cafe_backend.wrapper.UserWrapper(u.id,u.name," +
        "u.email,u.contactNumber,u.status) FROM User u WHERE u.role = 'user'")
@NamedQuery(name ="User.updateStatus", query = "UPDATE User u set u.status= :status WHERE u.id= :id")

// Déclaration de l'entité JPA. Cette annotation indique que la classe est une entité persistante.
@Entity

// Annotation pour permettre l'insertion dynamique des colonnes qui ne sont pas nulles
@DynamicInsert

// Annotation pour permettre la mise à jour dynamique des colonnes qui ont changé
@DynamicUpdate

@Data
@Table(name = "user")
public class User implements Serializable {

    // Déclaration d'un identifiant unique pour la sérialisation
    @Serial
    private static final long serialVersionUID = 1L;

    // Annotation pour spécifier que ce champ est la clé primaire de l'entité
    @Id
    // Annotation pour définir la stratégie de génération de valeur pour la clé primaire (IDENTITY utilise une auto-incrémentation)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Spécifie le nom de la colonne dans la base de données associée à ce champ
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;


    @Column(name = "role")
    private String role;

    private String resetToken;

    private LocalDateTime tokenExpirationDate;

}
