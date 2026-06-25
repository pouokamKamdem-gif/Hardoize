package com.dignequipe.hardoiz.dettes.DetteFournisseur;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Data
public interface DetteFournisseurRepository extends JpaRepository<DetteFournisseur, Long> {
    List<DetteFournisseur> findAllByActiveTrue();
    List<DetteFournisseur> findByFournisseurId(Long fournisseurId);
}
