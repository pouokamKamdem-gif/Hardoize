package com.dignequipe.hardoiz.produits;

import com.dignequipe.hardoiz.pointsvente.PointDeVente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    // code unique (code bare de reference interne)
    @Column(unique = true)
    private String code;

    private double prixVente;

    private double prixAchat;

    // stock simple (unite de base)
    private Integer stock;

    private Integer seuilAlerte;

    private boolean actif = true;

    // boutiaue proprietaire du produit
    @ManyToOne
    private PointDeVente pointDeVente;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;
}
