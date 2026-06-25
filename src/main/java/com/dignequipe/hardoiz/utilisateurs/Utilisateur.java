package com.dignequipe.hardoiz.utilisateurs;

import com.dignequipe.hardoiz.pointsvente.roles.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true)
    private String nomUtilisateur;
    private String motDePasse;

    private String telephone;
    private boolean actif;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<UtilisateurPointVente> boutiques;
}
