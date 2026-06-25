package com.dignequipe.hardoiz.produits;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByPointDeVenteIdAndActifTrue(Long pointDeVenteId);
}
