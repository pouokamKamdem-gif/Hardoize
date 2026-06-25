package com.dignequipe.hardoiz.produits.fourniisseurs.achats.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LigneAchatRequestDTO {

    private Long produitId;

    private Integer quantite;

    private Double prixUnitaire;
}
