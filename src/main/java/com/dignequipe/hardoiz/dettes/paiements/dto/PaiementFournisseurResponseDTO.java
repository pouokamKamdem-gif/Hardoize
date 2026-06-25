package com.dignequipe.hardoiz.dettes.paiements.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaiementFournisseurResponseDTO {
    private Long id;
    private Double montant;
    private LocalDateTime datePaiement;
    private Long detteFournisseurId;
    private Long utilisateurId;
}
