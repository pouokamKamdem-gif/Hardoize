package com.dignequipe.hardoiz.dettes.DetteClient;

import com.dignequipe.hardoiz.clients.Client;
import com.dignequipe.hardoiz.dettes.DetteClient.dto.DetteClientRequestDTO;
import com.dignequipe.hardoiz.clients.ClientRepository;
import com.dignequipe.hardoiz.utilisateurs.UtilisateurRepository;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetteClientService {

    private final DetteClientRepository detteClientRepository;
    private final ClientRepository clientRepository;
    private final UtilisateurRepository utilisateurRepository;

    public DetteClientService(
            DetteClientRepository detteClientRepository,
            ClientRepository clientRepository,
            UtilisateurRepository utilisateurRepository
    ){
        this.detteClientRepository = detteClientRepository;
        this.clientRepository = clientRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public DetteClient creer(DetteClientRequestDTO dto) {
        Client client = clientRepository.findById(dto.getClientId()).orElseThrow(() -> new RuntimeException("Client introuvable!"));

        Utilisateur utilisateur = utilisateurRepository.findById(dto.getUtilisateurId()).orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));

        DetteClient detteClient = new DetteClient();

        detteClient.setClient(client);
        detteClient.setUtilisateur(utilisateur);

        detteClient.setMontantInitial(dto.getMontantInitial());
        detteClient.setMontantPaye(0.0);
        detteClient.setMontantRestant(dto.getMontantInitial());

        detteClient.setActive(true);

        return detteClientRepository.save(detteClient);
    }

    public List<DetteClient> getAll() {
        return detteClientRepository.findAll();
    }
    public DetteClient getById(Long id) {
        return detteClientRepository.findById(id).orElseThrow(() -> new RuntimeException("Dette introuvable !"));
    }
    public List<DetteClient> getActives() {
        return detteClientRepository.findByActiveTrue();
    }
    public List<DetteClient>  getByClient(Long clientId) {
        return detteClientRepository.findByClientIdOrderByDateCreationDesc(clientId);
    }
    public DetteClient  update(Long id, DetteClient dto) {
        DetteClient detteClient = getById(id);

        detteClient.setMontantPaye(dto.getMontantPaye());

        double restant = detteClient.getMontantInitial() - dto.getMontantPaye();
        detteClient.setMontantRestant(restant);

        detteClient.setActive(restant > 0);

        return detteClientRepository.save(detteClient);
    }
    public void delete(Long id){
        DetteClient detteClient = getById(id);
        detteClient.setActive(false);
        detteClientRepository.save(detteClient);
    }
}
