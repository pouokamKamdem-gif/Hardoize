package com.dignequipe.hardoiz.produits.fourniisseurs.achats;

import com.dignequipe.hardoiz.produits.fourniisseurs.Fournisseur;
import com.dignequipe.hardoiz.pointsvente.PointDeVente;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "achats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Achat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @ManyToOne
    @JoinColumn(name = "point_vente_id")
    private PointDeVente pointDeVente;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;


    private LocalDateTime dateAchat;

    private Double montantTotal;

    private Double montantPaye;

    private Double montantRestant;

    private boolean achatACredit;

    @OneToMany(
            mappedBy = "achat",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LigneAchat> lignes = new ArrayList<>();
}
