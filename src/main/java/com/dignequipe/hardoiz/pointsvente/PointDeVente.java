package com.dignequipe.hardoiz.pointsvente;

import com.dignequipe.hardoiz.utilisateurs.UtilisateurPointVente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointDeVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String adresse;

    private String ville;

    @Enumerated(EnumType.STRING)
    private TypeCommerce typeCommerce;// DETAIL pour ce cas

    @OneToMany(mappedBy = "pointDeVente")
    private List<UtilisateurPointVente> utilisateurs;

    private LocalDateTime dateCreation;
    private boolean actif;
}
