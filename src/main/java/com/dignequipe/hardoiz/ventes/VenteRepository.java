package com.dignequipe.hardoiz.ventes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {
    List<Vente> findByPointDeVenteId(Long boutiqueId);
}
