package com.dignequipe.hardoiz.dettes.paiements.dto;

import lombok.Data;

@Data
public class PaiementClientRequestDTO {

    private Long detteClientId;

    private Double montant;

    private String commentaire;
}
