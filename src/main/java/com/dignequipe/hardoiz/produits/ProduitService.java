package com.dignequipe.hardoiz.produits;

import com.dignequipe.hardoiz.pointsvente.PointDeVente;
import com.dignequipe.hardoiz.produits.dto.ProduitRequestDTO;
import com.dignequipe.hardoiz.pointsvente.PointDeVenteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProduitService {
    private final ProduitRepository produitRepository;
    private final PointDeVenteRepository pointDeVenteRepository;

    public ProduitService(ProduitRepository produitRepository, PointDeVenteRepository pointDeVenteRepository) {
        this.produitRepository = produitRepository;
        this.pointDeVenteRepository = pointDeVenteRepository;
    }

    // creation
    public Produit creerProduit(ProduitRequestDTO dto) {

        PointDeVente boutique = pointDeVenteRepository.findById(dto.pointDeVenteId).orElseThrow(() -> new RuntimeException("Boutique introuvable"));

        Produit produit = new Produit();
        produit.setNom(dto.nom);
        produit.setCode(dto.code);
        produit.setPrixAchat(dto.prixAchat);
        produit.setPrixVente(dto.prixVente);
        produit.setStock(dto.stock);
        produit.setSeuilAlerte(dto.seuilAlerte);
        produit.setActif(true);
        produit.setPointDeVente(boutique);
        produit.setDateCreation(LocalDateTime.now());
        produit.setDateModification(LocalDateTime.now());

        return produitRepository.save(produit);
    }

    //lister tout (par POINT DE VENTE)
    public List<Produit> lister(Long boutiqueId) {
        return produitRepository.findByPointDeVenteIdAndActifTrue(boutiqueId);
    }

    // lister par ID
    public Produit getById(Long id){
        return produitRepository.findById(id).orElseThrow(() -> new RuntimeException("Produit introuvable !"));
    }

    // Modification
    public Produit modifier(Long id, ProduitRequestDTO dto) {

        Produit produit = getById(id);

        produit.setNom(dto.nom);
        produit.setCode(dto.code);
        produit.setPrixAchat(dto.prixAchat);
        produit.setPrixVente(dto.prixVente);
        produit.setStock(dto.stock);
        produit.setSeuilAlerte(dto.seuilAlerte);
        produit.setDateModification(LocalDateTime.now());

        return produitRepository.save(produit);
    }

   // suppression logique
    public void desactiver(Long id){
        Produit produit = getById(id);

        produit.setActif(false);
        produit.setDateModification(LocalDateTime.now());

        produitRepository.save(produit);
    }
}
