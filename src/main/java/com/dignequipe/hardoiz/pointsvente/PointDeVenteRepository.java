package com.dignequipe.hardoiz.pointsvente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointDeVenteRepository extends JpaRepository<PointDeVente, Long> {
    @Override
    List<PointDeVente> findAll();
}
