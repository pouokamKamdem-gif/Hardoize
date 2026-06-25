package com.dignequipe.hardoiz.utilisateurs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByNomUtilisateur(String nomUtilisateur);
    List<Utilisateur> findAll();
}
