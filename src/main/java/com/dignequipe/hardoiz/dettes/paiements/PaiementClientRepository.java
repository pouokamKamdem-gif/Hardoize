package com.dignequipe.hardoiz.dettes.paiements;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementClientRepository extends JpaRepository<PaiementClient, Long> {
    List<PaiementClient> findByDetteClientIdOrderByDatePaiementDesc(Long detteClientId);
}
