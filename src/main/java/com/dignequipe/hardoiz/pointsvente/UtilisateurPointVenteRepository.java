package com.dignequipe.hardoiz.pointsvente;

import com.dignequipe.hardoiz.utilisateurs.UtilisateurPointVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilisateurPointVenteRepository extends JpaRepository<UtilisateurPointVente, Long> {
    List<UtilisateurPointVente> findByUtilisateurId(Long utilisateurId);
}
