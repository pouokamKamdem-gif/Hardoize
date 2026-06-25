package com.dignequipe.hardoiz.dettes.paiements.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaiementFournisseurRequestDTO {
    private Double montant;
    private Long detteFournisseurId;
    private Long utilisateurId;
    private LocalDateTime datePaiement;
}
