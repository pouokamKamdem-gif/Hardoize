package com.dignequipe.hardoiz.ventes;

import com.dignequipe.hardoiz.clients.Client;
import com.dignequipe.hardoiz.pointsvente.PointDeVente;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //le point de vente concernee par la vente
    @ManyToOne
    private PointDeVente pointDeVente;

    //utilisateur qui effectue la vente (caissier/vendeur)
    @ManyToOne
    private Utilisateur utilisateur;

    //client optionel(possible sans client)
    @ManyToOne(optional = true)
    private Client client;

    // date de la vente
    private LocalDateTime dateVente;

    //total de la vente
    private double total;

    // type de vente CASH ou CREDIT
    @Enumerated(EnumType.STRING)
    private TypeVente typeVente;

    // liste des produits vendus
    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL)
    private List<LigneVente> lignes;
}
