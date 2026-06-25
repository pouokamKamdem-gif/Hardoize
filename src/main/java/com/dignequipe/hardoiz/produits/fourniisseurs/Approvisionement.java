package com.dignequipe.hardoiz.produits.fourniisseurs;

import com.dignequipe.hardoiz.produits.Produit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Approvisionement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Fournisseur fournisseur;

    @ManyToOne
    private Produit produit;

    private int quantite;
    private double prixAchat;

    private Date date;
}
