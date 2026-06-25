package com.dignequipe.hardoiz.produits.fourniisseurs.achats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneAchatRepository extends JpaRepository<LigneAchat, Long> {
}
