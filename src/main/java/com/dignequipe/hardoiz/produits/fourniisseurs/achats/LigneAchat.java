package com.dignequipe.hardoiz.produits.fourniisseurs.achats;

import com.dignequipe.hardoiz.produits.Produit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ligne_achats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LigneAchat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantite;

    private Double prixUnitaire;

    private Double sousTotal;

    @ManyToOne
    @JoinColumn(name = "achat_id")
    private Achat achat;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;
}
