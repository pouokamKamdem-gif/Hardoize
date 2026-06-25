package com.dignequipe.hardoiz.dettes.DetteFournisseur;

import com.dignequipe.hardoiz.dettes.paiements.PaiementFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementFournisseurRepository extends JpaRepository<PaiementFournisseur, Long> {
    List<PaiementFournisseur> findByDetteFournisseurId(Long detteFournisseurId);

    List<PaiementFournisseur> findByUtilisateurId(Long utilisateurId);
}
