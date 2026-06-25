package com.dignequipe.hardoiz.produits.fourniisseurs.achats;

import com.dignequipe.hardoiz.dettes.DetteFournisseur.DetteFournisseur;
import com.dignequipe.hardoiz.dettes.DetteFournisseur.DetteFournisseurRepository;
import com.dignequipe.hardoiz.produits.fourniisseurs.Fournisseur;
import com.dignequipe.hardoiz.produits.fourniisseurs.FournisseurRepository;
import com.dignequipe.hardoiz.produits.fourniisseurs.achats.dto.AchatRequestDTO;
import com.dignequipe.hardoiz.produits.fourniisseurs.achats.dto.LigneAchatRequestDTO;
import com.dignequipe.hardoiz.pointsvente.PointDeVente;
import com.dignequipe.hardoiz.pointsvente.PointDeVenteRepository;
import com.dignequipe.hardoiz.produits.Produit;
import com.dignequipe.hardoiz.produits.ProduitRepository;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import com.dignequipe.hardoiz.utilisateurs.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AchatService {
    private final AchatRepository achatRepository;
    private final ProduitRepository produitRepository;
    private final FournisseurRepository fournisseurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PointDeVenteRepository pointDeVenteRepository;
    private final DetteFournisseurRepository detteFournisseurRepository;

    public Achat creer(AchatRequestDTO dto) {
        Fournisseur fournisseur = fournisseurRepository.findById(dto.getFournisseurId()).orElseThrow(() -> new RuntimeException("Fournisseur introuvable !"));
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getUtilisisateurId()).orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));
        PointDeVente pointDeVente = pointDeVenteRepository.findById(dto.getPointDeVenteId()).orElseThrow(() -> new RuntimeException("Point de vente introuvable !"));

        Achat achat = new Achat();
        achat.setDateAchat(LocalDateTime.now());
        achat.setFournisseur(fournisseur);
        achat.setUtilisateur(utilisateur);
        achat.setPointDeVente(pointDeVente);

        List<LigneAchat> lignes = new ArrayList<>();

        double montantTotal = 0.0;

        for (LigneAchatRequestDTO ligneDto : dto.getLignes()){
            Produit produit = produitRepository.findById(ligneDto.getProduitId()).orElseThrow(() -> new RuntimeException("Prosuit introuvable"));

            LigneAchat ligne = new LigneAchat();

            ligne.setProduit(produit);
            ligne.setQuantite(ligneDto.getQuantite());
            ligne.setPrixUnitaire(ligneDto.getPrixUnitaire());

            double sousTotal = ligneDto.getQuantite() * ligneDto.getPrixUnitaire();
            ligne.setSousTotal(sousTotal);
            ligne.setAchat(achat);

            lignes.add(ligne);

            montantTotal += sousTotal;

            produit.setStock(
                    produit.getStock() + ligneDto.getQuantite()
            );

            produitRepository.save(produit);
        }

        achat.setLignes(lignes);

        achat.setMontantTotal(montantTotal);

        Double montantPaye = dto.getMontantPaye() == null ? 0.0 : dto.getMontantPaye();

        achat.setMontantRestant(montantTotal - montantPaye);
        achat.setAchatACredit(montantPaye < montantTotal);

        Achat achatSauvegarde = achatRepository.save(achat);

        if(achatSauvegarde.getAchatACredit()) {
            DetteFournisseur detteFournisseur = new DetteFournisseur();

            detteFournisseur.setAchat(achatSauvegarde);
            detteFournisseur.setFournisseur(fournisseur);
            detteFournisseur.setUtilisateur(utilisateur);

            detteFournisseur.setMontantInitial(montantTotal);
            detteFournisseur.setMontantPaye(montantPaye);
            detteFournisseur.setMontantRestant(montantTotal - montantPaye);
            detteFournisseur.setDateCreation(LocalDateTime.now());
            detteFournisseur.setDateModification(LocalDateTime.now());

            detteFournisseur.setActive(true);

            detteFournisseurRepository.save(detteFournisseur);
        }

        return achatSauvegarde;
    }

    public List<Achat> getAll(){
        return achatRepository.findAll();
    }

    public Achat getById(Long id) {
        return achatRepository.findById(id).orElseThrow(() -> new RuntimeException("Achat introuvable !"));
    }

    public void delete(Long id) {
        achatRepository.deleteById(id);
    }
}
