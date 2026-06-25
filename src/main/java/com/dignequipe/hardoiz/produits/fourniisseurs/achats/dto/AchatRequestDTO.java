package com.dignequipe.hardoiz.produits.fourniisseurs.achats.dto;

import lombok.Data;

import java.util.List;

@Data
public class AchatRequestDTO {
    private Long fournisseurId;

    private Long utilisisateurId;

    private Long PointDeVenteId;

    private Double montantPaye;

    private List<LigneAchatRequestDTO> lignes;
}
