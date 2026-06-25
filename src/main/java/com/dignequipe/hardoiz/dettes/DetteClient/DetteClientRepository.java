package com.dignequipe.hardoiz.dettes.DetteClient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetteClientRepository extends JpaRepository<DetteClient, Long> {
    List<DetteClient> findByClientIdOrderByDateCreationDesc(Long ClientId);
    List<DetteClient> findByActiveTrue();
}
