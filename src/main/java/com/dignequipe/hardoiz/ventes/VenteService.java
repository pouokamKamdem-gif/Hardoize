package com.dignequipe.hardoiz.ventes;

import com.dignequipe.hardoiz.clients.Client;
import com.dignequipe.hardoiz.clients.ClientRepository;
import com.dignequipe.hardoiz.dettes.DetteClient.DetteClient;
import com.dignequipe.hardoiz.dettes.DetteClient.DetteClientRepository;
import com.dignequipe.hardoiz.pointsvente.PointDeVente;
import com.dignequipe.hardoiz.pointsvente.PointDeVenteRepository;
import com.dignequipe.hardoiz.produits.Produit;
import com.dignequipe.hardoiz.produits.ProduitRepository;
import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import com.dignequipe.hardoiz.utilisateurs.UtilisateurRepository;
import com.dignequipe.hardoiz.ventes.dto.LigneVenteDTO;
import com.dignequipe.hardoiz.ventes.dto.VenteRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VenteService {
    private final VenteRepository venteRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PointDeVenteRepository pointDeVenteRepository;
    private final ClientRepository clientRepository;
    private final DetteClientRepository detteClientRepository;

    public VenteService(VenteRepository venteRepository, ProduitRepository produitRepository,
                        UtilisateurRepository utilisateurRepository,
                        PointDeVenteRepository pointDeVenteRepository,
                        ClientRepository clientRepository,
                        DetteClientRepository detteClientRepository) {
        this.venteRepository = venteRepository;
        this.produitRepository = produitRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.pointDeVenteRepository = pointDeVenteRepository;
        this.clientRepository = clientRepository;
        this.detteClientRepository = detteClientRepository;
    }

    //creation vente complete
    @Transactional
    public Vente creerVente(VenteRequestDTO dto) {

        //utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(dto.utilisateurId).orElseThrow(()-> new RuntimeException("Utiisateur introuvale !"));

        // boutique
        PointDeVente boutique = pointDeVenteRepository.findById(dto.pointDeVenteId).orElseThrow(() -> new RuntimeException("Boutique introuvale !"));

        // client optionel
        Client client = null;
        if(dto.clientId != null) {
            client = clientRepository.findById(dto.clientId).orElse(null);
        }

        //creation de la vente
        Vente vente = new Vente();
        vente.setUtilisateur(utilisateur);
        vente.setPointDeVente(boutique);
        vente.setClient(client);
        vente.setDateVente(LocalDateTime.now());
        // mode de paiement
        vente.setTypeVente(TypeVente.valueOf(dto.typeVente));

        double total = 0;
        // traitement des lignes
        List<LigneVente> lignes = new ArrayList<>();

        for (LigneVenteDTO l : dto.getLignes()) {

            //recuperation produit depuis DB
            Produit produit = produitRepository.findById(l.produitId).orElseThrow(() -> new RuntimeException("Produit introuvable!"));

            // verification stock
            if (produit.getStock() <  l.getQuantite()){
                throw new RuntimeException("Stock insufisant : " + produit.getNom());
            }

            produit.setStock(produit.getStock() - l.getQuantite());
            produitRepository.save(produit);

            LigneVente ligne = new LigneVente();
            ligne.setProduit(produit);
            ligne.setQuantite(l.getQuantite());
            ligne.setPrixUnitaire(l.getPrixUnitaire());

            //cacul du sous total (ligne)
            double sousTotal = produit.getPrixVente() * l.getQuantite();
            ligne.setSousTotal(sousTotal);

            //lien avec vente
            ligne.setVente(vente);
            lignes.add(ligne);

            total += sousTotal;
        }

        vente.setLignes(lignes);
        vente.setTotal(total);

        // dette logique
        if (vente.getTypeVente() == TypeVente.CREDIT) {
            if (client == null) {
                throw new RuntimeException("Client obligatoire pour vente à credit !");
            }
            DetteClient dette = new DetteClient();
            // ajouter dette
            dette.setClient(client);
            dette.setVente(vente);
            dette.setMontantInitial(total);
            dette.setMontantRestant(total);
            dette.setActive(true);
            dette.setDateCreation(LocalDateTime.now());
            dette.setDateModification(LocalDateTime.now());

            dette.setVente(vente);// lien a la vente

            detteClientRepository.save(dette);
        }
        //sauvegarder
        return venteRepository.save(vente);
    }

    // lister toute les VENTES
    public List<Vente> getAllVentes() {
        return venteRepository.findAll();
    }

    // lister par BOUTIQUE
    public List<Vente> getVentesByBoutique(Long boutiqueId) {
        return venteRepository.findByPointDeVenteId(boutiqueId);
    }

    //lister par ID
    public Vente getVenteById(Long id) {
        return venteRepository.findById(id).orElseThrow(() -> new RuntimeException("Vente introuvable"));
    }

    // suppression
    public void deleteVente(Long id) {
        venteRepository.deleteById(id);
    }
}
