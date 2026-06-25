package com.dignequipe.hardoiz.dettes.paiements;

import com.dignequipe.hardoiz.dettes.DetteClient.DetteClient;
import com.dignequipe.hardoiz.dettes.paiements.dto.PaiementClientRequestDTO;
import com.dignequipe.hardoiz.dettes.DetteClient.DetteClientRepository;
import com.dignequipe.hardoiz.utilisateurs.UtilisateurRepository;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
public class PaiementClientService {

    private final PaiementClientRepository paiementRepository;
    private final DetteClientRepository detteClientRepository;
    private final UtilisateurRepository utilisateurRepository;

    public PaiementClientService(
            PaiementClientRepository paiementRepository,
            DetteClientRepository detteClientRepository,
            UtilisateurRepository utilisateurRepository
    ){
        this.paiementRepository = paiementRepository;
        this.detteClientRepository = detteClientRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional
    public PaiementClient payerDetteClient(PaiementClientRequestDTO dto, Long utilisateurId) {

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElseThrow(() -> new RuntimeException("utilisateur introuvable !"));
        DetteClient dette = detteClientRepository.findById(dto.getDetteClientId()).orElseThrow(() -> new RuntimeException("Dette introuvable "));

        if (dto.getMontant() <= 0) {
            throw new RuntimeException("Montant invalide !");
        }
        if(dto.getMontant() > dette.getMontantRestant()) {
            throw new RuntimeException("Montant superieur à la dette restante !");
        }

        // ceation paiement
        PaiementClient paiementClient = new PaiementClient();
        paiementClient.setMontant(dto.getMontant());
        paiementClient.setCommentaire(dto.getCommentaire());
        paiementClient.setDatePaiement(LocalDateTime.now());
        paiementClient.setUtilisateur(utilisateur);
        paiementClient.setDetteClient(dette);
        paiementClient.setDateCreation(LocalDateTime.now());

        //reduction dette
        dette.setMontantRestant(dette.getMontantRestant() - dto.getMontant());
        if (dette.getMontantRestant() == 0){
            dette.setActive(false);
        }
        dette.setDateModification(LocalDateTime.now());

        detteClientRepository.save(dette);

        return paiementRepository.save(paiementClient);
    }

    //get par dette
    public List<PaiementClient> getByDette(Long detteClientId) {
        return paiementRepository.findByDetteClientIdOrderByDatePaiementDesc(detteClientId);
    }
}
