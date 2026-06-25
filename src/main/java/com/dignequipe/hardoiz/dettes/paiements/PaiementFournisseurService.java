package com.dignequipe.hardoiz.dettes.paiements;

import com.dignequipe.hardoiz.dettes.DetteFournisseur.DetteFournisseur;
import com.dignequipe.hardoiz.dettes.paiements.dto.PaiementFournisseurRequestDTO;
import com.dignequipe.hardoiz.dettes.paiements.dto.PaiementFournisseurResponseDTO;
import com.dignequipe.hardoiz.dettes.DetteFournisseur.DetteFournisseurRepository;
import com.dignequipe.hardoiz.dettes.DetteFournisseur.PaiementFournisseurRepository;
import com.dignequipe.hardoiz.utilisateurs.UtilisateurRepository;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;

import java.time.LocalDateTime;
import java.util.List;

public class PaiementFournisseurService {

    private final PaiementFournisseurRepository paiementFournisseurRepository;
    private final DetteFournisseurRepository detteFournisseurRepository;
    private final UtilisateurRepository utilisateurRepository;

    public PaiementFournisseurService(
            PaiementFournisseurRepository paiementFournisseurRepository,
            DetteFournisseurRepository detteFournisseurRepository,
            UtilisateurRepository utilisateurRepository
    ){
        this.paiementFournisseurRepository = paiementFournisseurRepository;
        this.detteFournisseurRepository = detteFournisseurRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public PaiementFournisseurResponseDTO creer(PaiementFournisseurRequestDTO dto){

        DetteFournisseur dette = detteFournisseurRepository.findById(dto.getDetteFournisseurId()).orElseThrow(() -> new RuntimeException("Dette fournisseur introuvable !"));
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getUtilisateurId()).orElseThrow(() -> new RuntimeException("Utilisateur introuble !"));

        PaiementFournisseur paiementFournisseur = new PaiementFournisseur();
        paiementFournisseur.setMontant(dto.getMontant());
        paiementFournisseur.setDatePaiement(dto.getDatePaiement());

        paiementFournisseur.setDetteFournisseur(dette);
        paiementFournisseur.setUtilisateur(utilisateur);

        PaiementFournisseur sauvegarde = paiementFournisseurRepository.save(paiementFournisseur);

        // Mise a jour la dette
            if(dto.getMontant() > dette.getMontantRestant()) {
                throw new RuntimeException("Le montant du paiement depasse le reste à payer.");
            } else {
                dette.setMontantPaye(dette.getMontantPaye() + dto.getMontant());
                dette.setMontantRestant(dette.getMontantInitial() - dette.getMontantPaye());
                dette.setDateModification(LocalDateTime.now());
                dette.setActive(!dette.getMontantRestant().equals(0.0));

                detteFournisseurRepository.save(dette);
            }
        return convertirEnResponseDTO(sauvegarde);
    };

    public List<PaiementFournisseurResponseDTO> getALl(){
        return paiementFournisseurRepository.findAll().stream().map(this::convertirEnResponseDTO).toList();
    };

    public PaiementFournisseurResponseDTO getById(Long id){
        PaiementFournisseur paiementFournisseur = paiementFournisseurRepository.findById(id).orElseThrow(() -> new RuntimeException("Paiement introuvable !"));

        return convertirEnResponseDTO(paiementFournisseur);
    };

    public List<PaiementFournisseurResponseDTO> getByDetteFournisseur(Long detteId){
        return paiementFournisseurRepository.findByDetteFournisseurId(detteId).stream().map(this::convertirEnResponseDTO).toList();
    };

    public List<PaiementFournisseurResponseDTO> getByUtilisateur(Long utilisateurId){
        return paiementFournisseurRepository.findByUtilisateurId(utilisateurId).stream().map(this::convertirEnResponseDTO).toList();
    }

    private PaiementFournisseurResponseDTO convertirEnResponseDTO(PaiementFournisseur paiementFournisseur){
        PaiementFournisseurResponseDTO dto = new PaiementFournisseurResponseDTO();

        dto.setId(paiementFournisseur.getId());
        dto.setMontant(paiementFournisseur.getMontant());
        dto.setDatePaiement(paiementFournisseur.getDatePaiement());

        dto.setDetteFournisseurId(paiementFournisseur.getDetteFournisseur().getId());
        dto.setUtilisateurId(paiementFournisseur.getUtilisateur().getId());

        return dto;
    }
}
