package com.dignequipe.hardoiz.ventes;

import com.dignequipe.hardoiz.produits.Produit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LigneVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Produit produit;
    private int quantite;

    private double prixUnitaire;
    private double sousTotal;

    @ManyToOne
    private Vente vente;
}
