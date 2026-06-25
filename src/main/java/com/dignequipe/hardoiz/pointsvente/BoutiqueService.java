package com.dignequipe.hardoiz.pointsvente;

import com.dignequipe.hardoiz.utilisateurs.RoleBoutique;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import com.dignequipe.hardoiz.utilisateurs.UtilisateurPointVente;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoutiqueService {
    private final PointDeVenteRepository pointDeVenteRepository;
    private final UtilisateurPointVenteRepository utilisateurPointVenteRepository;

    public BoutiqueService(PointDeVenteRepository pointDeVenteRepository,
                           UtilisateurPointVenteRepository utilisateurPointVenteRepository) {
        this.pointDeVenteRepository = pointDeVenteRepository;
        this.utilisateurPointVenteRepository = utilisateurPointVenteRepository;
    }

    @Transactional
    public PointDeVente creerBoutique(PointDeVente boutique, Utilisateur proprietaire) {
        // 1. sauvegarde de la boutique
        PointDeVente savedBoutique = pointDeVenteRepository.save(boutique);

        // 2. creer liaison propritaire - boutique
        UtilisateurPointVente liaison = new UtilisateurPointVente();
        liaison.setUtilisateur(proprietaire);
        liaison.setPointDeVente(savedBoutique);
        liaison.setRole(RoleBoutique.PROPRIETAIRE);

        utilisateurPointVenteRepository.save(liaison);

        return savedBoutique;
    }
    public List<PointDeVente> boutiqueUtilisateur(Long utilisateurId) {
        List<UtilisateurPointVente> liens = utilisateurPointVenteRepository.findByUtilisateurId(utilisateurId);
        return liens.stream().map(UtilisateurPointVente::getPointDeVente).toList();
    }

    public List<PointDeVente> getAll(){
        return pointDeVenteRepository.findAll();
    }
}
