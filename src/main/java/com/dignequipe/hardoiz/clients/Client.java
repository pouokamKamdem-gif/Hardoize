package com.dignequipe.hardoiz.clients;

import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    private String telephone;
    private String adresse;

    // si le client est aussi un utilisateur
    @OneToOne
    private Utilisateur utilisateur;

    private boolean actif = true;
}
