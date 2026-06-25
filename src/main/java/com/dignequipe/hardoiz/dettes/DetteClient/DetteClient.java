package com.dignequipe.hardoiz.dettes.DetteClient;

import com.dignequipe.hardoiz.clients.Client;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import com.dignequipe.hardoiz.ventes.Vente;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class DetteClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Utilisateur utilisateur;

    @ManyToOne
    private Vente vente;

    private Double montantInitial;
    private Double montantPaye;
    private Double montantRestant;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    private Boolean active;
}
