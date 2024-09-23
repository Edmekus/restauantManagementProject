package com.cafe_backend.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;


@NamedQuery(name = "Bill.getAllBills", query = "select b from Bill b order by b.id desc")

@NamedQuery(name = "Bill.getBillByUserName",query = "select b from Bill b where b.createdBy=:username order by b.id desc")


@Entity
@DynamicInsert
@DynamicUpdate
@Data
@Table(name = "bill")
public class Bill implements Serializable {

    // Déclaration d'un identifiant unique pour la sérialisation
    @Serial
    private static final long serialVersionUID = 1234L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String name;

    private String email;

    private String contactNumber;

    private String paymentMethod;

    private Integer total;

    @Column(name = "productDetails", columnDefinition = "json")
    private String productDetails;

    private String createdBy;
}
