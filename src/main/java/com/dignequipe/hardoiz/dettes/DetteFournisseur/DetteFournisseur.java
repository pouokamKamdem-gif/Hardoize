package com.dignequipe.hardoiz.dettes.DetteFournisseur;

import com.dignequipe.hardoiz.produits.fourniisseurs.Fournisseur;
import com.dignequipe.hardoiz.produits.fourniisseurs.achats.Achat;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class DetteFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Fournisseur fournisseur;

    @ManyToOne
    private Utilisateur utilisateur;

    @ManyToOne
    private Achat achat;

    private Double montantInitial;
    private Double montantPaye;
    private Double montantRestant;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    private Boolean active = true;
}
