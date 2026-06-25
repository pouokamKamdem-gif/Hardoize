package com.dignequipe.hardoiz.dettes.paiements;

import com.dignequipe.hardoiz.dettes.DetteClient.DetteClient;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PaiementClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;

    private LocalDateTime datePaiement;

    private String reference;

    private String commentaire;

    //utilisateur qui encaisse
    @ManyToOne
    private Utilisateur utilisateur;

    //dette client
    @ManyToOne
    private DetteClient detteClient;

    private LocalDateTime dateCreation;
}
