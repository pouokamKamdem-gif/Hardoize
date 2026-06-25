package com.dignequipe.hardoiz.dettes.DetteFournisseur;

import com.dignequipe.hardoiz.produits.fourniisseurs.Fournisseur;
import com.dignequipe.hardoiz.produits.fourniisseurs.FournisseurRepository;

import java.time.LocalDateTime;
import java.util.List;

public class DetteFournisseurService {
    private final DetteFournisseurRepository detteFournisseurRepository;
    private final FournisseurRepository fournisseurRepository;

    public DetteFournisseurService(
            DetteFournisseurRepository detteFournisseurRepository,
            FournisseurRepository fournisseurRepository
    ){
        this.detteFournisseurRepository = detteFournisseurRepository;
        this.fournisseurRepository = fournisseurRepository;
    }

    //creer dette fournisseur
    public DetteFournisseur creerDette(Long fournisseurId, Double montant){
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId).orElseThrow(() -> new RuntimeException("Fournisseur introuvable !"));

        DetteFournisseur dette = new DetteFournisseur();

        dette.setFournisseur(fournisseur);
        dette.setMontantInitial(montant);
        dette.setMontantRestant(montant);
        dette.setDateCreation(LocalDateTime.now());
        dette.setDateModification(LocalDateTime.now());
        dette.setActive(true);

        return detteFournisseurRepository.save(dette);
    }

    //dettes fournisseur
    public List<DetteFournisseur> getByFournisseur(Long fournisseurId){
        return detteFournisseurRepository.findByFournisseurId(fournisseurId);
    }

    //reduire dette (paiement sortant)
    public DetteFournisseur reduireDette(Long detteId, Double montant) {

        DetteFournisseur dette = detteFournisseurRepository.findById(detteId).orElseThrow(() -> new RuntimeException("Dette fournisseur introuvable !"));
        if(montant > dette.getMontantRestant()) {
            throw new RuntimeException("Montant superieur à la dette");
        }

        dette.setMontantRestant(dette.getMontantRestant() - montant);
        if(dette.getMontantRestant() == 0) {
            dette.setActive(false);
        }

        return detteFournisseurRepository.save(dette);
    }
}
