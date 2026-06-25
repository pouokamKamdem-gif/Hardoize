package com.dignequipe.hardoiz.clients;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public Client creer(Client client){
        return clientRepository.save(client);
    }

    public List<Client> getAll(){
        return clientRepository.findAll();
    }

    public Client getById(Long id){
        return clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client introuvable !"));
    }

    public Client update(Long id, Client updated){
        Client c = getById(id);
        c.setNom(updated.getNom());
        c.setPrenom(updated.getPrenom());
        c.setAdresse(updated.getAdresse());
        c.setTelephone(updated.getTelephone());

        return clientRepository.save(c);
    }

    public void delete(Long clientId){
        Client c = getById(clientId);
        c.setActif(false);
    }
}
