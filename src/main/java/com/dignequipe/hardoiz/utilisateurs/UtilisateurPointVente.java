package com.dignequipe.hardoiz.utilisateurs;

import com.dignequipe.hardoiz.pointsvente.PointDeVente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurPointVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    private Utilisateur utilisateur;

    @ManyToOne
    private PointDeVente pointDeVente;

    @Enumerated(EnumType.STRING)
    private RoleBoutique role;
    // OWNER, MANAGER, CAISSIER, etc
}
