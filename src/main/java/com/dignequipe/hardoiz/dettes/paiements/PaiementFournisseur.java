package com.dignequipe.hardoiz.dettes.paiements;

import com.dignequipe.hardoiz.dettes.DetteFournisseur.DetteFournisseur;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PaiementFournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;

    private LocalDateTime datePaiement;

    @ManyToOne
    private DetteFournisseur detteFournisseur;

    @ManyToOne
    @JoinColumn(name = "dette_fournisseur_id")
    private PaiementFournisseur paiementFournisseur;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
